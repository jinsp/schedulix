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


#ifndef MD
#define MD 5
#endif

#include <stdio.h>
#include <time.h>
#include <string.h>
#include "global.h"
#if MD == 2
#include "md2.h"
#endif
#if MD == 4
#include "md4.h"
#endif
#if MD == 5
#include "md5.h"
#endif

#define TEST_BLOCK_LEN 1000
#define TEST_BLOCK_COUNT 1000

static void MDString PROTO_LIST ((char *));
static void MDTimeTrial PROTO_LIST ((void));
static void MDTestSuite PROTO_LIST ((void));
static void MDFile PROTO_LIST ((char *));
static void MDFilter PROTO_LIST ((void));
static void MDPrint PROTO_LIST ((unsigned char [16]));

#if MD == 2
#define MD_CTX MD2_CTX
#define MDInit MD2Init
#define MDUpdate MD2Update
#define MDFinal MD2Final
#endif
#if MD == 4
#define MD_CTX MD4_CTX
#define MDInit MD4Init
#define MDUpdate MD4Update
#define MDFinal MD4Final
#endif
#if MD == 5
#define MD_CTX MD5_CTX
#define MDInit MD5Init
#define MDUpdate MD5Update
#define MDFinal MD5Final
#endif

int main (argc, argv)
int argc;
char *argv[];
{
	int i;

	if (argc > 1)
		for (i = 1; i < argc; i++)
			if (argv[i][0] == '-' && argv[i][1] == 's')
				MDString (argv[i] + 2);
			else if (strcmp (argv[i], "-t") == 0)
				MDTimeTrial ();
			else if (strcmp (argv[i], "-x") == 0)
				MDTestSuite ();
			else
				MDFile (argv[i]);
	else
		MDFilter ();

	return (0);
}

static void MDString (string)
char *string;
{
	MD_CTX context;
	unsigned char digest[16];
	unsigned int len = strlen (string);

	MDInit (&context);
	MDUpdate (&context, string, len);
	MDFinal (digest, &context);

	printf ("MD%d (\"%s\") = ", MD, string);
	MDPrint (digest);
	printf ("\n");
}

static void MDTimeTrial ()
{
	MD_CTX context;
	time_t endTime, startTime;
	unsigned char block[TEST_BLOCK_LEN], digest[16];
	unsigned int i;

	printf ("MD%d time trial. Digesting %d %d-byte blocks ...", MD,
	        TEST_BLOCK_LEN, TEST_BLOCK_COUNT);

	for (i = 0; i < TEST_BLOCK_LEN; i++)
		block[i] = (unsigned char)(i & 0xff);

	time (&startTime);

	MDInit (&context);
	for (i = 0; i < TEST_BLOCK_COUNT; i++)
		MDUpdate (&context, block, TEST_BLOCK_LEN);
	MDFinal (digest, &context);

	time (&endTime);

	printf (" done\n");
	printf ("Digest = ");
	MDPrint (digest);
	printf ("\nTime = %ld seconds\n", (long)(endTime-startTime));
	printf ("Speed = %ld bytes/second\n",
	        (long)TEST_BLOCK_LEN * (long)TEST_BLOCK_COUNT/(endTime-startTime));
}

static void MDTestSuite ()
{
	printf ("MD%d test suite:\n", MD);

	MDString ("");
	MDString ("a");
	MDString ("abc");
	MDString ("message digest");
	MDString ("abcdefghijklmnopqrstuvwxyz");
	MDString ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
	MDString ("1234567890123456789012345678901234567890\
			1234567890123456789012345678901234567890");
}

static void MDFile (filename)
char *filename;
{
	FILE *file;
	MD_CTX context;
	int len;
	unsigned char buffer[1024], digest[16];

	if ((file = fopen (filename, "rb")) == NULL)
		printf ("%s can't be opened\n", filename);

	else {
		MDInit (&context);
		while (len = fread (buffer, 1, 1024, file))
			MDUpdate (&context, buffer, len);
		MDFinal (digest, &context);

		fclose (file);

		printf ("MD%d (%s) = ", MD, filename);
		MDPrint (digest);
		printf ("\n");
	}
}

static void MDFilter ()
{
	MD_CTX context;
	int len;
	unsigned char buffer[16], digest[16];

	MDInit (&context);
	while (len = fread (buffer, 1, 16, stdin))
		MDUpdate (&context, buffer, len);
	MDFinal (digest, &context);

	MDPrint (digest);
	printf ("\n");
}

static void MDPrint (digest)
unsigned char digest[16];
{
	unsigned int i;

	for (i = 0; i < 16; i++)
		printf ("%02x", digest[i]);
}

