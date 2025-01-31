package database;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JAVA_DATABASE_C_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbOutputTerminal out = getOutputTerminal("out");

        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Create a new empty message for the output
            MbMessage outMessage = new MbMessage();
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
            
            // ----------------------------------------------------------
            // Add user code below for XML to JSON transformation and SELECT query
            
            // Getting the root element from the input assembly (XML)
            MbElement InputRoot = inAssembly.getMessage().getRootElement();
            
            // Establish JDBC connection "{jdbce_retrive_police}:jdbc_retrive"
            
            conn = getJDBCType4Connection("jdbce_retrive_police}:jdbc_retrive", JDBC_TransactionType.MB_TRANSACTION_AUTO);

            // Prepare SQL SELECT query (adjust as needed)
            String sqlQuery = "SELECT * FROM STUDENT";
            pstmt = conn.prepareStatement(sqlQuery);

            // Execute the query
            rs = pstmt.executeQuery();

            // Set the domain to XML for the output message
            MbElement OutputRoot = outMessage.getRootElement();
            OutputRoot.createElementAsLastChild(MbElement.TYPE_NAME, "XMLNSC", null);

            // Creating the XML message structure for the output
            MbElement dataRoot = OutputRoot.createElementAsLastChild(MbElement.TYPE_NAME, "Stu", null);

            // Iterate through the result set and construct XML
            while (rs.next()) {
                // Create an element for each student
                MbElement studentElement = dataRoot.createElementAsLastChild(MbElement.TYPE_NAME, "Stu", null);

                // Retrieve each column and add it to the XML structure
                //studentElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "ID", rs.getString("ID"));
                studentElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Name", rs.getString("Name"));
                studentElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Age", rs.getString("Age")); // Add other fields as necessary
            }

            // End of user code
            // ----------------------------------------------------------

        } catch (MbException e) {
            // Re-throw to allow Broker handling of MbException
            throw e;
        } catch (SQLException e) {
            // Handle SQL exception
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        } catch (RuntimeException e) {
            // Re-throw to allow Broker handling of RuntimeException
            throw e;
        } catch (Exception e) {
            // Ensure all exceptions are re-thrown to be handled in the flow
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        } finally {
            // Close resources
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // Handle close exception
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // Handle close exception
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Handle close exception
                }
            }
        }

        // Propagate the transformed message to the 'out' terminal
        out.propagate(outAssembly);
    }
}