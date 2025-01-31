package javasimple;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbElement;

public class JAVA_COMPUTE_SIM_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbOutputTerminal out = getOutputTerminal("out");
        MbOutputTerminal alt = getOutputTerminal("alternate");

        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;
        try {
            // Create new message as a copy of the input
            MbMessage outMessage = new MbMessage(inMessage);
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
           
            // ----------------------------------------------------------
            // Add user code below
           
            // Getting the root element from the input assembly
            MbElement InputRoot = inAssembly.getMessage().getRootElement();
           
            // Fetching the "id" and "name" from the XML message using XPath
            MbElement id = InputRoot.getFirstElementByPath("XMLNSC/student/id");
            MbElement name = InputRoot.getFirstElementByPath("XMLNSC/student/name");

            // Getting the root element from the output assembly
            MbElement OutputRoot = outAssembly.getMessage().getRootElement().getLastChild();

            // Creating a "Response" element as the last child of OutputRoot
            MbElement student = OutputRoot.createElementAsLastChild(MbElement.TYPE_NAME, "Response", null);

            // Creating "ID" and "Name" elements under "Response" and setting their values
            MbElement S_id = student.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "ID", id.getValue());
            MbElement S_name = student.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Name", name.getValue());

            // Detaching the first child of OutputRoot (clean-up step)
            OutputRoot.getFirstChild().detach();
           
            // End of user code
            // ----------------------------------------------------------

        } catch (MbException e) {
            // Re-throw to allow Broker handling of MbException
            throw e;
        } catch (RuntimeException e) {
            // Re-throw to allow Broker handling of RuntimeException
            throw e;
        } catch (Exception e) {
            // Consider replacing Exception with type(s) thrown by user code
            // Example handling ensures all exceptions are re-thrown to be handled in the flow
            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        }
        // The following should only be changed
        // if not propagating message to the 'out' terminal
        out.propagate(outAssembly);
    }
}