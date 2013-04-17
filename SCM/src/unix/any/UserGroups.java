/*
 * ===========================================================================
 * Licensed Materials - Property of IBM
 *
 * (C) Copyright IBM Corp. 2004 All Rights Reserved.
 *
 *  US Government Users Restricted Rights - Use, duplication or
 *  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * ===========================================================================
 *
 */

/*
 * The "unix.any" package denotes what operating system and version
 * the collector runs on. In this sample, it denotes that this collector
 * is able to run on any "unix" operating system and version.
 */
package unix.any;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Vector;
import java.util.StringTokenizer;
import java.sql.Types;
/*
 * The "com.ibm.jac" package contains the CollectorV2 and Message classes.
 */
import com.ibm.jac.*;

/*
 *  This collector collects group and group member information from the
 *  /etc/group file.
 */
public class UserGroups extends com.ibm.jac.CollectorV2 {
    /*
     * Define the release number of the collector.
     *
     * This number is used to track changes to the collector and is
     * increased each time the collector is changed.
     */
    private static final int RELEASE = 1;

    /*
     * Define the names of the database tables for this collector.
     *
     * Name the table with a name closely related to the collector's class
     * name for easy identification. The table name must be 30 characters or
     * less in length and contain all capital letters.
     *
     * By convention, the start of the table name indicates the operating
     * system or application. End the table name with its version number,
     * such as:  _V1, _V2, _V14
     *
     * Collector tables are created in the database when the collector
     * is registered on the server. Collector tables reside in the
     * JAC_DATA schema. Use JAC_DATA.your_table_name to reference the
     * table in compliance queries.
     *
     * This sample defines two database tables:
     *
     *        UNIX_GROUPS_V1
     *        UNIX_GROUPS_MEMBERS_V1
     *
     */
    private static final String[] TABLENAME =
        {
                        "UNIX_GROUPS_V1",
                        "UNIX_GROUP_MEMBERS_V1"
            };

    /* Define a description for the collector.
     *
     * The description is displayed when the collector is installed and
     * when the collector is displayed in the administration console.
     * By convention, the description starts with the word "Description:"
     * and consists of a short sentence describing the function of the
     * collector.
     */
    private static final String DESCRIPTION =
        "Description: Collects group and group member information from the /etc/group file.";

    /*
     * Define a list of compatible operating systems for the collector.
     *
     * This sample collector runs only on UNIX systems.
     *
     */
    private static final String[] COMPATIBLE_OS =
        {
                "AIX",
                "HP-UX",
                "LINUX",
                "SUNOS"
                };

    /*
     * Define the columns for the collector's database table.
     *
     * For each column provide the following information:
     *
     *   name        : The name of the column. Column names
     *                 are 30 or less alphanumeric characters
     *                 in length.
     *   type        : The type of data in the column. See
     *                 java.sql.Types for valid values.
     *   size        : The size of the data in the column. Not
     *                 required for some data types, in which
     *                 case, the value specified is ignored.
     *   constraints : DB2 SQL constraints on the column.
     *                 Only "not null" is supported.
     *
     * This sample defines the column definitions for two collector tables:
     *
     *          UNIX_GROUPS_V1
     *          UNIX_GROUP_MEMBERS_V1
     */
    private static final CollectorTable.Column[][] TABLE_DEFINITION = { {
            /*
             * Table: UNIX_GROUPS_V1
             * Column name ruler:      0123ABCDEFGHIJKLMNOPQRSTUVWXYZ
             */
            new CollectorTable.Column("GROUP_NAME", Types.VARCHAR, 50),
            new CollectorTable.Column("GID", Types.BIGINT, 0)},
                {
            /*
             * Table: UNIX_GROUP_MEMBERS_V1
             * Column name ruler:      0123ABCDEFGHIJKLMNOPQRSTUVWXYZ
             */
            new CollectorTable.Column("GID", Types.BIGINT, 0),
            new CollectorTable.Column("USER_NAME", Types.VARCHAR, 32)
            }
    };

    /*
     * Define the UNIX system configuration file where the data is to be
     * collected from.
     */
    private static final String DEFAULT_FILENAME = "/etc/group";

    /*
     * Define the comment character in the /etc/group file.
     */
    private static final String COMMENT_CHAR = "#";

    /*
     *  Define the field separator character in the /etc/group file.
     */
    private static final char FIELD_SEPARATOR = ':';

    /*
     * Define the user separator character in the /etc/group file.
     */
    private static final String USER_SEPARATOR_CHAR = ",";

    /*
     * Required default constructor for this collector.
     */
    public UserGroups() {
    }

    /*
     * Required method: getCompatibleOS().
     *
     * Returns a String array containing the list of the operating systems
     * that the collector can run on.
     *
     * This method is defined as abstract in the CollectorV2 class and must
     * be implemented by any collector.
     */
    public String[] getCompatibleOS() {
        return COMPATIBLE_OS;
    }

    /*
     * Required method: getReleaseNumber().
     *
     * Returns the release number of the collector.
     *
     * This method is defined as abstract in the CollectorV2 class and must
     * be implemented by any collector.
     */
    public int getReleaseNumber() {
        return RELEASE;
    }

    /*
     * Required method: getDescription().
     *
     * Returns a String containing the description of the collector.
     *
     * This method is defined as abstract in the CollectorV2 class and must
     * be implemented by any user defined collector.
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /*
     * Required method: getParameters().
     *
     * Returns a Vector of Strings containing the names of the parameters
     * supported by the collector. Return an empty Vector if no parameters
     * are supported.
     *
     * This method is defined as abstract in the CollectorV2 class and must
     * be implemented by any collector.
     */
    public Vector getParameters() {
        /*
         * In the sample, an empty Vector is returned because this collector
         * has no parameters associated with it.
         */
        return new Vector();
    }

    /*
     * Required method: getTables().
     *
     * Returns an array of class CollectorTable that describes the
     * structure of the collector database tables.  Each database
     * table defined by the collector is represented by an array
     * element.
     *
     * This method is defined as abstract in the CollectorV2 class and must
     * be implemented by any collector.
     */
    public CollectorTable[] getTables() {
        CollectorTable[] tables = new CollectorTable[TABLENAME.length];

        for (int i = 0; i < TABLENAME.length; i++) {
            tables[i] = new CollectorTable(TABLENAME[i]);
            for (int j = 0; j < TABLE_DEFINITION[i].length; j++) {
                tables[i].addColumn(TABLE_DEFINITION[i][j]);
            }
        }
        return tables;
    }

    /*
     * Required method: executeV2().
     *
     * Gathers compliance data from the client.
     *
     * Compliance data is returned as an array of class Message. Each
     * array element represents the data for a database table.
     *
     * This method is defined as abstract in the CollectorV2 class and must
     * be implemented by any collector.
     */
    public Message[] executeV2() {
        Message[] messages;
        Vector[] columns;
        CollectorTable[] tables;
        /*
         * record1 is the record for the first table.
         */
        Object[] record1;
        /*
         * record2 is the record for the second table.
         */
        Object[] record2;
        String[] headers;
        BufferedReader infile = null;
        File etcGroup = null;

        /*
         * Allocate an array of Vectors to contain the database
         * table column information for this collector. Allocate
         * a vector for each database table used by the collector.
        */
        messages = new Message[TABLENAME.length];

        /*
         * Allocate the array of Message objects to be returned from the
         * collector. Allocate a Message object for each database table.
         */
        columns = new Vector[TABLENAME.length];

        /*
         * Get the table information for this collector.
         */
        tables = getTables();

        /*
         * Instantiate the message array.
         */
        for (int i = 0; i < TABLENAME.length; i++) {
            messages[i] = new Message(TABLENAME[i]);
            columns[i] = tables[i].getColumns();

            /*
             * Fill in the column headers for the current table.
             */
            headers = new String[columns[i].size()];
            for (int j = 0; j < columns[i].size(); j++) {
                headers[j] =
                 ((CollectorTable.Column) columns[i].elementAt(j)).getName();
            }
            messages[i].getDataVector().addElement(headers);
        }

        try {
            etcGroup = new File(DEFAULT_FILENAME);
            /*
             * If the file etc/group exists
             */
            if (etcGroup.exists()) {
                try {
                    infile = new BufferedReader(new FileReader(etcGroup));
                }
                catch (Exception e) {
                    /*
                     * Return an error message indicating a failure occurred
                     * attempting to open the /etc/group file. The error
                     * message is logged in the client message log.
                     */
                    return new Message[]
                    {
                        errorMessage("An error occurred opening the file: "
                        + DEFAULT_FILENAME)
                    };
                }
            }
            else {
                /*
                 * Return an error message indicating that the
                 * /etc/group file does not exist.
                 */
                return new Message[]
                {
                        errorMessage("The required file " + DEFAULT_FILENAME +
                                        " does not exist.")
                                };
            }

            String line = "";
            StringTokenizer st = null;
            boolean isEmptyFile = true;
            Vector ovTokens = null;

            /*
             * Read the contents of the /etc/group file.
             */
            while ((line = infile.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(COMMENT_CHAR) || line.length() == 0)
                    continue;

                /*
                 * Allocate an Object array for the record for the first table.
                 */
                record1 = new Object[columns[0].size()];

                ovTokens = getAllTokens(line, FIELD_SEPARATOR);

                /*
                 * Skip any entries that are not valid.
                 */
                if (ovTokens.size() != 4 ||
                        ((String) ovTokens.elementAt(0)).trim().length() == 0) {
                    continue;
                }

                /*
                 * Add the group name to the record.
                 */
                record1[0] = ((String) ovTokens.elementAt(0)).trim();

                try {
                    /*
                     * Add the group ID to the record.
                     */
                    record1[1] =
                        new Long(((String) ovTokens.elementAt(2)).trim());
                }
                catch (NumberFormatException ne) {
                    continue;
                }

                isEmptyFile = false;
                /*
                 * Add the record to the Message object for the first table.
                 */
                messages[0].getDataVector().addElement(record1);

                String users = ((String) ovTokens.elementAt(3)).trim();

                if (users.length() == 0)
                    continue;

                StringTokenizer userListTokenizer =
                        new StringTokenizer(users, USER_SEPARATOR_CHAR);
                while (userListTokenizer.hasMoreTokens()) {
                    /*
                     * Allocate an Object array for the record for the
                     * second table.
                     */
                    record2 = new Object[columns[1].size()];
                    /*
                     * Add the group ID to the second record.
                     */
                    record2[0] = record1[1];
                    String user = userListTokenizer.nextToken();
                    /*
                     * Add the user name to the second record.
                     */
                    record2[1] = user.trim();
                    /*
                     * Add the record to the Message object for the second
                     * table.
                     */
                    messages[1].getDataVector().addElement(record2);
                }

            }

            if (isEmptyFile) {
                return new Message[]
                {
                    /*
                     * Return an error message indicating that no valid data
                     * was found in the /etc/group file.
                     */
                     errorMessage("The required file " + DEFAULT_FILENAME +
                                 " exists but does not contain valid data.")
                                };
            }
        }
        catch (IOException e)
        {
            return new Message[]
            {
                /*
                 * Return an error message indicating that an
                 * error occurred reading the /etc/group file.
                 */
                errorMessage("An error occurred reading the file:" +
                DEFAULT_FILENAME)
            };
        }
        catch (Exception e) {
              /*
               * Return an error message indicating that an unexpected
               * error occurred during collector processing.
               */
            return new Message[]
            {
                errorMessage("An unexpected error occurred. The exception was: " +
                                          e.getMessage())
            };
        }
        finally {
            exit(this, "executeV2");
            if (infile != null) {
                try {
                    infile.close();
                }
                catch (IOException e) {
                    /*
                     * Do Nothing
                     */
                }
            }
        }
        /*
         * Return the gathered collector data.
         */
        return messages;
    }

    /*
     * Tokenizes the input string and returns all the tokens in a vector.
     * Returns an empty string as an element of the vector if two delimiters are together.
     *
     * @param s                 input string to be tokenized
     * @param ch                delimiter
     * @return a vector of tokens. returns null if error encountered.
     */
    private static Vector getAllTokens(String s, char ch) {
        Vector v = new Vector();
        String str = null;
        int bIdx = 0, idx = 0;

        try {
            while (true) {
                if ((idx = s.indexOf(ch, bIdx)) < 0) {
                    str = s.substring(bIdx);
                    v.add(str);
                    break;
                }
                str = s.substring(bIdx, idx);
                v.add(str);
                bIdx = idx + 1;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }
        return v;
    }
}