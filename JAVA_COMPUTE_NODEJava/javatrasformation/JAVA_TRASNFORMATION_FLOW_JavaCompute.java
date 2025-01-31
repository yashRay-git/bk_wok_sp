package javatrasformation;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class JAVA_TRASNFORMATION_FLOW_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
    	
        MbOutputTerminal out = getOutputTerminal("out");

        MbOutputTerminal alt = getOutputTerminal("alternate");

        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;
        try {
            // Create a new empty message for the output
            MbMessage outMessage = new MbMessage();
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
            
            // ----------------------------------------------------------
            // Add user code below for XML to JSON transformation
            
            // Getting the root element from the input assembly (XML)
            MbElement InputRoot = inAssembly.getMessage().getRootElement();
            
            // Fetching the "id" and "name" from the XML message using XPath
            MbElement id = InputRoot.getFirstElementByPath("XMLNSC/student/id");
            MbElement name = InputRoot.getFirstElementByPath("XMLNSC/student/name");

            // Clear the OutputRoot and set the domain to JSON
            MbElement OutputRoot = outMessage.getRootElement();
            
            OutputRoot.createElementAsLastChild("JSON");

           // OutputRoot.createElementAsLastChild(MbElement.TYPE_NAME, "JSON", null);

            // Getting the root element from the output assembly
            MbElement OutputRoot1 = outAssembly.getMessage().getRootElement().getLastChild();

            
            // Creating the JSON message structure
            MbElement jsonData = OutputRoot1.createElementAsLastChild(MbElement.TYPE_NAME, "Data", null);
            MbElement jsonStudent = jsonData.createElementAsLastChild(MbElement.TYPE_NAME, "student", null);

            // Adding "id" and "name" fields in JSON format
            jsonStudent.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "ID", id.getValue());
            jsonStudent.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Name", name.getValue());

            // End of user code
            // ----------------------------------------------------------

        } catch (MbException e) {
            // Re-throw to allow Broker handling of MbException
            throw e;
        } catch (RuntimeException e) {
            // Re-throw to allow Broker handling of RuntimeException
            throw e;
        } catch (Exception e) {
            // Ensure all exceptions are re-thrown to be handled in the flow
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        }

        // Propagate the transformed message to the 'out' terminal
        out.propagate(outAssembly);
    }
}
