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
import de.independit.scheduler.server.output.*;

public class AlterServer extends Node
{

	public final static String __version = "@(#) $Id: AlterServer.java,v 2.3.6.1 2013/03/14 10:24:23 ronald Exp $";

	int traceLevel;
	boolean doSchedule;

	public AlterServer(Integer a)
	{
		super();
		traceLevel = a.intValue();
		doSchedule = false;
		txMode = SDMSTransaction.READONLY;
	}

	public AlterServer()
	{
		super();
		doSchedule = true;
		txMode = SDMSTransaction.READWRITE;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		if(!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			Iterator i = sysEnv.cEnv.gid().iterator();
			SDMSPrivilege p = new SDMSPrivilege();
			while (i.hasNext()) {
				Long gId = (Long) i.next();
				try {
					SDMSGrant g = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(SDMSProxy.ZERO, gId));
					p.addPriv(sysEnv, g.getPrivs(sysEnv).longValue());
				} catch (NotFoundException nfe) {

				}
			}
			if (!p.can(SDMSPrivilege.MANAGE_SYS))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03202081832", "Insufficient Privileges"));
		}

		if (doSchedule) {
			sysEnv.sched.requestReschedule();
			result.setFeedback(new SDMSMessage(sysEnv, "03805141212", "Resource reschedule requested"));
		} else {
			ThreadGroup tg;
			SDMSThread[]    list;
			int i, nt;

			tg = env.getMe().getThreadGroup();
			list = new SDMSThread[tg.activeCount()];
			nt = tg.enumerate(list);

			for(i=0; i<nt; i++) {
				if(list[i] instanceof ListenThread) break;
			}
			if(i >= nt) {

				return;
			}
			SystemEnvironment.setTraceLevel(traceLevel);
			switch(traceLevel) {
			case 0:
				((ListenThread) list[i]).trace_off();
				SDMSException.debugOff();
				result.setFeedback(new SDMSMessage(sysEnv, "03203191018", "Server Trace disabled"));
				break;
			case 1:
				SDMSException.debugOff();
				result.setFeedback(new SDMSMessage(sysEnv, "03203191017", "Server Trace shows warnings"));
				break;
			case 2:
				SDMSException.debugOff();
				result.setFeedback(new SDMSMessage(sysEnv, "03203191019", "Server Trace shows messages"));
				break;
			default:
				SDMSException.debugOn();
				result.setFeedback(new SDMSMessage(sysEnv, "03203191020", "Server Trace shows everything"));
				break;
			}
		}
	}

}

