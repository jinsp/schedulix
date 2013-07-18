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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSEvent extends SDMSEventProxyGeneric
	implements SDMSOwnedObject
{

	public final static String __version = "SDMSEvent $Revision: 2.4.6.1 $ / @(#) $Id: SDMSEvent.java,v 2.4.6.1 2013/03/14 10:25:18 ronald Exp $";

	protected SDMSEvent(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long seId = getSeId(sysEnv);
		String se = null;
		if(seId != null)
			se = SDMSSchedulingEntityTable.getObject(sysEnv, seId).pathString(sysEnv);

		return getName(sysEnv) + (se == null ? "" : " (" + se + ")");
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "event " + getURLName(sysEnv);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Long id = getId(sysEnv);

		Vector v = SDMSScheduledEventTable.idx_evtId.getVector(sysEnv, id);
		if (v.size() != 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03710161540",
			                               "Cannot drop, scheduled events present"));
		}
		super.delete(sysEnv);
	}

}
