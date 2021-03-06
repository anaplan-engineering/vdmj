/*******************************************************************************
 *
 *	Copyright (c) 2017 Fujitsu Services Ltd.
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

package com.fujitsu.vdmjc.xml;

import java.util.Properties;

public class XMLTagNode extends XMLNode
{
	public final String tag;
	public final Properties attrs;

	public XMLTagNode(String tag, Properties attrs)
	{
		this.tag = tag;
		this.attrs = attrs;
	}

	public String getAttr(String name)
	{
		return attrs.getProperty(name);		// Can be null
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(tag);

		for (Object name: attrs.keySet())
		{
			sb.append(" ");
			sb.append(name);
			sb.append("=\"");
			sb.append(attrs.get(name));
			sb.append("\"");
		}

		sb.append("/>");
		return sb.toString();
	}
}
