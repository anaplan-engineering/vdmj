/*******************************************************************************
 *
 *	Copyright (c) 2016 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.fujitsu.vdmj.in.definitions;

import com.fujitsu.vdmj.in.expressions.INEqualsExpression;
import com.fujitsu.vdmj.in.expressions.INExpression;
import com.fujitsu.vdmj.in.patterns.INPattern;
import com.fujitsu.vdmj.runtime.Context;
import com.fujitsu.vdmj.runtime.StateContext;
import com.fujitsu.vdmj.tc.lex.TCNameToken;
import com.fujitsu.vdmj.tc.types.TCField;
import com.fujitsu.vdmj.tc.types.TCFieldList;
import com.fujitsu.vdmj.tc.types.TCRecordType;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.util.Utils;
import com.fujitsu.vdmj.values.FunctionValue;
import com.fujitsu.vdmj.values.State;

/**
 * A class to hold a module's state definition.
 */
public class INStateDefinition extends INDefinition
{
	private static final long serialVersionUID = 1L;
	public final TCFieldList fields;
	public final INPattern invPattern;
	public final INExpression invExpression;
	public final INPattern initPattern;
	public final INExpression initExpression;
	public final INExplicitFunctionDefinition invdef;
	public final INExplicitFunctionDefinition initdef;
	public final boolean canBeExecuted;

	public FunctionValue invfunc = null;
	public FunctionValue initfunc = null;

	private final INDefinitionList statedefs;
	private TCRecordType recordType;
	private State moduleState = null;

	public INStateDefinition(TCNameToken name, TCFieldList fields,
		INPattern invPattern, INExpression invExpression,
		INPattern initPattern, INExpression initExpression,
		INExplicitFunctionDefinition invdef, INExplicitFunctionDefinition initdef,
		boolean canBeExecuted)
	{
		super(name.getLocation(), null, name);

		this.fields = fields;
		this.invPattern = invPattern;
		this.invExpression = invExpression;
		this.initPattern = initPattern;
		this.initExpression = initExpression;
		this.invdef = invdef;
		this.initdef = initdef;
		this.canBeExecuted = canBeExecuted;

		statedefs = new INDefinitionList();

		for (TCField f: fields)
		{
			statedefs.add(new INLocalDefinition(f.tagname.getLocation(), f.tagname, f.type, null));
			INLocalDefinition ld = new INLocalDefinition(f.tagname.getLocation(), f.tagname.getOldName(), f.type, null);
			statedefs.add(ld);
		}

		recordType = new TCRecordType(name, fields, false);
		INLocalDefinition recordDefinition = null;

		recordDefinition = new INLocalDefinition(location, name, recordType, null);
		statedefs.add(recordDefinition);

		recordDefinition = new INLocalDefinition(location, name.getOldName(), recordType, null);
		statedefs.add(recordDefinition);
	}

	@Override
	public String toString()
	{
		return "state " + name + " of\n" + Utils.listToString(fields, "\n") +
				(invPattern == null ? "" : "\n\tinv " + invPattern + " == " + invExpression) +
	    		(initPattern == null ? "" : "\n\tinit " + initPattern + " == " + initExpression) +
	    		"\nend";
	}

	@Override
	public INDefinition findName(TCNameToken sought)
	{
		if (invdef != null && invdef.findName(sought) != null)
		{
			return invdef;
		}

		if (initdef != null && initdef.findName(sought) != null)
		{
			return initdef;
		}

		for (INDefinition d: statedefs)
		{
			INDefinition def = d.findName(sought);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	@Override
	public INExpression findExpression(int lineno)
	{
		if (invExpression != null)
		{
			INExpression found = invExpression.findExpression(lineno);
			if (found != null) return found;
		}

		if (initExpression != null)
		{
			if (initExpression instanceof INEqualsExpression)
			{
				INEqualsExpression ee = (INEqualsExpression)initExpression;
				INExpression found = ee.right.findExpression(lineno);
				if (found != null) return found;
			}
		}

		return null;
	}

	@Override
	public TCType getType()
	{
		return recordType;
	}

	public void initState(StateContext initialContext)
	{
		if (invdef != null)
		{
			invfunc = new FunctionValue(invdef, null, null, initialContext);
			initialContext.put(name.getInvName(location), invfunc);
		}

		if (initdef != null)
		{
			initfunc = new FunctionValue(initdef, null, null, initialContext);
			initialContext.put(name.getInitName(location), initfunc);
		}

		moduleState = new State(this);
		moduleState.initialize(initialContext);
	}

	public Context getStateContext()
	{
		return moduleState.getContext();
	}

	public State getState()
	{
		return moduleState;
	}
}
