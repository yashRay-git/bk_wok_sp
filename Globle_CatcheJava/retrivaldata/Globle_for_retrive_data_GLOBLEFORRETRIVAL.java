package retrivaldata;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbGlobalMap;
import com.ibm.broker.plugin.MbXMLNSC;
import com.ibm.broker.plugin.MbUserException;

public class Globle_for_retrive_data_GLOBLEFORRETRIVAL extends MbJavaComputeNode {
    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbMessage inMessage = inAssembly.getMessage();
        MbMessage outMessage = new MbMessage();
        MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);

        try {
            // Access InputRoot and Employee object from the message
            MbElement inputRoot = inMessage.getRootElement().getLastChild();
            MbElement empObject = inputRoot.getFirstElementByPath("Employee");

            // Extract the ID from Employee
            String ID = empObject.getFirstElementByPath("ID").getValueAsString();
            String val = null;

            // Access global map to retrieve the value
            MbGlobalMap globalmap = MbGlobalMap.getGlobalMap("Mymap");
            val = (String) globalmap.get(ID); 

            // Create OutputRoot in the response message
            MbElement outputRoot = outMessage.getRootElement().createElementAsFirstChild(MbXMLNSC.PARSER_NAME);
            MbElement responseObject = outputRoot.createElementAsFirstChild(MbXMLNSC.FOLDER, "Response", "");

            // Add ID and Name fields to the response
            responseObject.createElementAsFirstChild(MbXMLNSC.FIELD, "ID", ID);
            responseObject.createElementAsFirstChild(MbXMLNSC.FIELD, "Name", val);

        } catch (MbException e) {
            // Handle MbException specifically
            throw e;
        } catch (RuntimeException e) {
            // Handle runtime exceptions
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        } finally {
            // Propagate the output message assembly
            getOutputTerminal("out").propagate(outAssembly);
        }
    }
}
