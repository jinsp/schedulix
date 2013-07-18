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

public class SDMSGrantTableGeneric extends SDMSTable
{

	public final static String __version = "SDMSGrantTableGeneric $Revision: 2.8 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "GRANTS";
	public static SDMSGrantTable table  = null;

	public static SDMSIndex idx_objectId;
	public static SDMSIndex idx_gId;
	public static SDMSIndex idx_objectId_gId;

	public SDMSGrantTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "Grant"));
		}
		table = (SDMSGrantTable) this;
		SDMSGrantTableGeneric.table = (SDMSGrantTable) this;
		isVersioned = false;
		idx_objectId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_gId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_objectId_gId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSGrant create(SystemEnvironment env
	                        ,Long p_objectId
	                        ,Long p_gId
	                        ,Integer p_objectType
	                        ,Long p_privs
	                        ,Long p_deleteVersion
	                       )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "Grant"));
		}
		validate(env
		         , p_objectId
		         , p_gId
		         , p_objectType
		         , p_privs
		         , p_deleteVersion
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSGrantGeneric o = new SDMSGrantGeneric(env
		                , p_objectId
		                , p_gId
		                , p_objectType
		                , p_privs
		                , p_deleteVersion
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                         );

		SDMSGrant p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSGrant)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSGrant)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSGrant p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_objectId
	                        ,Long p_gId
	                        ,Integer p_objectType
	                        ,Long p_privs
	                        ,Long p_deleteVersion
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSGrantGeneric.checkObjectType(p_objectType))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Grant: $1 $2", "objectType", p_objectType));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long objectId;
		Long gId;
		Integer objectType;
		Long privs;
		Long deleteVersion;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			objectId = new Long (r.getLong(2));
			gId = new Long (r.getLong(3));
			objectType = new Integer (r.getInt(4));
			privs = new Long (r.getLong(5));
			deleteVersion = new Long (r.getLong(6));
			if (r.wasNull()) deleteVersion = null;
			creatorUId = new Long (r.getLong(7));
			createTs = new Long (r.getLong(8));
			changerUId = new Long (r.getLong(9));
			changeTs = new Long (r.getLong(10));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "Grant: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSGrantGeneric(id,
		                            objectId,
		                            gId,
		                            objectType,
		                            privs,
		                            deleteVersion,
		                            creatorUId,
		                            createTs,
		                            changerUId,
		                            changeTs,
		                            validFrom, validTo);
	}

	protected void loadTable(SystemEnvironment env)
	throws SQLException, SDMSException
	{
		int read = 0;
		int loaded = 0;

		final String driverName = env.dbConnection.getMetaData().getDriverName();
		final boolean postgres = driverName.startsWith("PostgreSQL");
		String squote = "";
		String equote = "";
		if (driverName.startsWith("MySQL")) {
			squote = "`";
			equote = "`";
		}
		if (driverName.startsWith("Microsoft")) {
			squote = "[";
			equote = "]";
		}
		Statement stmt = env.dbConnection.createStatement();

		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   "ID" +
		                                   ", " + squote + "OBJECT_ID" + equote +
		                                   ", " + squote + "G_ID" + equote +
		                                   ", " + squote + "OBJECT_TYPE" + equote +
		                                   ", " + squote + "PRIVS" + equote +
		                                   ", " + squote + "DELETE_VERSION" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + tableName() +
		                                   ""						  );
		while(rset.next()) {
			if(loadObject(env, rset)) ++loaded;
			++read;
		}
		stmt.close();
		SDMSThread.doTrace(null, "Read " + read + ", Loaded " + loaded + " rows for " + tableName(), SDMSThread.SEVERITY_INFO);
	}

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_objectId.put(env, ((SDMSGrantGeneric) o).objectId, o);
		idx_gId.put(env, ((SDMSGrantGeneric) o).gId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGrantGeneric) o).objectId);
		k.add(((SDMSGrantGeneric) o).gId);
		idx_objectId_gId.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_objectId.remove(env, ((SDMSGrantGeneric) o).objectId, o);
		idx_gId.remove(env, ((SDMSGrantGeneric) o).gId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGrantGeneric) o).objectId);
		k.add(((SDMSGrantGeneric) o).gId);
		idx_objectId_gId.remove(env, k, o);
	}

	public static SDMSGrant getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSGrant) table.get(env, id);
	}

	public static SDMSGrant getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSGrant) table.get(env, id, version);
	}

	public static SDMSGrant idx_objectId_gId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSGrant)  SDMSGrantTableGeneric.idx_objectId_gId.getUnique(env, key);
	}

	public static SDMSGrant idx_objectId_gId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSGrant)  SDMSGrantTableGeneric.idx_objectId_gId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
