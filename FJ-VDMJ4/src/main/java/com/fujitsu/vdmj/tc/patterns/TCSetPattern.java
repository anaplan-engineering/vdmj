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

package com.fujitsu.vdmj.tc.patterns;

import java.util.List;
import java.util.Vector;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.tc.definitions.TCDefinitionList;
import com.fujitsu.vdmj.tc.lex.TCNameList;
import com.fujitsu.vdmj.tc.types.TCSetType;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.typechecker.Environment;
import com.fujitsu.vdmj.typechecker.NameScope;
import com.fujitsu.vdmj.typechecker.TypeCheckException;

public class TCSetPattern extends TCPattern
{
	private static final long serialVersionUID = 1L;
	public final TCPatternList plist;

	public TCSetPattern(LexLocation location, TCPatternList set)
	{
		super(location);
		this.plist = set;
	}

	@Override
	public void unResolve()
	{
		plist.unResolve();
		resolved = false;
	}

	@Override
	public void typeResolve(Environment env)
	{
		if (resolved) return; else { resolved = true; }

		try
		{
			plist.typeResolve(env);
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public String toString()
	{
		return "{" + plist.toString() + "}";
	}

	@Override
	public int getLength()
	{
		return plist.size();
	}

	@Override
	public TCDefinitionList getAllDefinitions(TCType type, NameScope scope)
	{
		TCDefinitionList defs = new TCDefinitionList();

		if (!type.isSet(location))
		{
			report(3204, "Set pattern is not matched against set type");
			detail("Actual type", type);
		}
		else
		{
			TCSetType set = type.getSet();

			if (!set.empty)
			{
        		for (TCPattern p: plist)
        		{
        			defs.addAll(p.getAllDefinitions(set.setof, scope));
        		}
			}
		}

		return defs;
	}

	@Override
	public TCNameList getAllVariableNames()
	{
		TCNameList list = new TCNameList();

		for (TCPattern p: plist)
		{
			list.addAll(p.getAllVariableNames());
		}

		return list;
	}

	@Override
	public TCType getPossibleType()
	{
		return new TCSetType(location, plist.getPossibleType(location));
	}

	@Override
	public List<TCIdentifierPattern> findIdentifiers()
	{
		List<TCIdentifierPattern> list = new Vector<TCIdentifierPattern>();

		for (TCPattern p: plist)
		{
			list.addAll(p.findIdentifiers());
		}

		return list;
	}
}
