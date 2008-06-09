package functionalTests.resourcemanager;

import java.io.File;

import org.junit.Before;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.resourcemanager.RMFactory;
import org.objectweb.proactive.extensions.resourcemanager.common.FileToBytesConverter;
import org.objectweb.proactive.extensions.resourcemanager.frontend.RMAdmin;
import org.objectweb.proactive.extensions.resourcemanager.frontend.RMMonitoring;
import org.objectweb.proactive.extensions.resourcemanager.frontend.RMUser;
import org.objectweb.proactive.gcmdeployment.GCMApplication;

import functionalTests.FunctionalTest;
import functionalTests.descriptor.variablecontract.javapropertiesDescriptor.Test;


public class FunctionalTDefaultRM extends FunctionalTest {

    protected RMUser user;
    protected RMAdmin admin;
    protected RMMonitoring monitor;

    private static String defaultDescriptor = Test.class.getResource(
            "/functionalTests/resourcemanager/GCMNodeSourceDeployment.xml").getPath();
    public int defaultDescriptorNodesNb = 5;

    @Before
    public void before() throws Exception {
        RMFactory.startLocal();
        user = RMFactory.getUser();
        admin = RMFactory.getAdmin();
        monitor = RMFactory.getMonitoring();
    }

    public void deployDefault() throws Exception {
        //admin.createGCMNodesource(new File(defaultDescriptor), "GCM_Node_Source");
        byte[] GCMDeploymentData = FileToBytesConverter.convertFileToByteArray((new File(defaultDescriptor)));
        admin.createGCMNodesource(GCMDeploymentData, "GCM_Node_Source");
    }
}
