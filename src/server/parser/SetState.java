/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

schedulix Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of schedulix

schedulix is free software:
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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class SetState extends Node
{

	public final static String __version = "@(#) $Id: SetState.java,v 2.1.4.1 2013/03/14 10:24:48 ronald Exp $";

	private String name;

	public SetState(String n)
	{
		super();
		cmdtype = Node.JOB_COMMAND;
		name = n;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, sysEnv.cEnv.uid());

		if(sme.getJobIsFinal(sysEnv).booleanValue()) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207251516", "Job must be in a pending state"));
		}
		if(sme.getJobIsRestartable(sysEnv).booleanValue()) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207251517", "Job must be in a pending state"));
		}

		long actVersion = sme.getSeVersion(sysEnv).longValue();
		Long esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, name, actVersion).getId(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		SDMSExitState es;
		try {
			es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(se.getEspId(sysEnv), esdId), actVersion);
		} catch (NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207251523", "State is not part of profile"));
		}

		sme.changeState(sysEnv, esdId, es, sme.getExitCode(sysEnv), sme.getErrorMsg(sysEnv), null );

		result.setFeedback(new SDMSMessage(sysEnv, "03207251503", "Status set"));
	}
}

