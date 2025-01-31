package globlecatche;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbGlobalMap;
import com.ibm.broker.plugin.MbXMLNSC;
import com.ibm.broker.plugin.MbUserException;

public class Globle_catche_memory_GLOBALCATCHE extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;

        try {
            // Create a new message as a copy of the input
            MbMessage outMessage = new MbMessage();
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);

            // Retrieve InputRoot and Employee object from the message
            MbElement inputRoot = inMessage.getRootElement().getLastChild();
            MbElement empObject = inputRoot.getFirstElementByPath("Employee");

            // Extract the Message, ID, and Name fields from Employee
            String message = empObject.getFirstElementByPath("Message").getValueAsString();
            String ID = empObject.getFirstElementByPath("ID").getValueAsString();
            String name = empObject.getFirstElementByPath("Name").getValueAsString();

            // Access the global map
            MbGlobalMap globalMap = MbGlobalMap.getGlobalMap("Mymap");
            // Update the global map based on the ID
            if (globalMap.containsKey(ID)) {
                globalMap.update(ID, name);
            } else {
                globalMap.put(ID, name);
            }

            // Create OutputRoot in the response message
            MbElement outputRoot = outMessage.getRootElement().createElementAsLastChild(MbXMLNSC.PARSER_NAME);
            MbElement responseObject = outputRoot.createElementAsLastChild(MbXMLNSC.FOLDER, "Response", "");

            // Add a success message to the response
            responseObject.createElementAsLastChild(MbXMLNSC.FIELD, "Message", "Data loaded successfully");

        } catch (MbException e) {
            // Handle message broker exceptions
            throw e;
        } catch (RuntimeException e) {
            // Handle runtime exceptions
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        } finally {
            // Propagate the output message assembly
            if (outAssembly != null) {
                getOutputTerminal("out").propagate(outAssembly);
            }
        }
    }
}
