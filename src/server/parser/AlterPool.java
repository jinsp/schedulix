/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
you can redistribute it and/or modify it under the terms of the
GNU Affero General Public License as published by the
Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/


package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class AlterPool extends Node
{

	public final static String __version = "@(#) $Id: AlterPool.java,v 2.8.4.3 2013/03/20 06:42:57 ronald Exp $";

	public AlterPool(ObjectURL u, WithHash w, Boolean r)
	{
		super();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_POOL);
	}
}

