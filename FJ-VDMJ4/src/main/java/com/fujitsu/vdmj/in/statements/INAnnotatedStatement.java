/*******************************************************************************
 *
 *	Copyright (c) 2018 Nick Battle.
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

package com.fujitsu.vdmj.in.statements;

import com.fujitsu.vdmj.Settings;
import com.fujitsu.vdmj.in.expressions.INExpression;
import com.fujitsu.vdmj.in.expressions.INExpressionList;
import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.messages.Console;
import com.fujitsu.vdmj.runtime.Context;
import com.fujitsu.vdmj.tc.lex.TCIdentifierToken;
import com.fujitsu.vdmj.values.Value;

public class INAnnotatedStatement extends INStatement
{
	private static final long serialVersionUID = 1L;

	public final TCIdentifierToken name;
	
	public final INExpressionList args;

	public final INStatement statement;
	
	public INAnnotatedStatement(LexLocation location, TCIdentifierToken name, INExpressionList args, INStatement statement)
	{
		super(location);
		this.name = name;
		this.args = args;
		this.statement = statement;
	}

	@Override
	public String toString()
	{
		return "@" + name + "(" + args + ") " + statement;
	}

	@Override
	public Value eval(Context ctxt)
	{
		if (Settings.annotations)
		{
			if (name.getName().equals("Trace"))
			{
				execTrace(ctxt);
			}
			else
			{
				abort(4173, "Unknown annotation @" + name, ctxt);
			}
		}
		
		return statement.eval(ctxt);
	}

	private void execTrace(Context ctxt)
	{
		if (args.isEmpty())
		{
			Console.err.println("Trace: " + location);
		}
		else
		{
			for (INExpression arg: args)
			{
				Value v = arg.eval(ctxt);
				Console.err.println("Trace: " + location + ", " + arg + " = " + v);
			}
		}
	}
}
