/**
 * ****************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 * <p/>
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * *****************************************************************************
 */
package io.cloudslang.lang.systemtests.flows;

import com.google.common.collect.Sets;
import io.cloudslang.lang.compiler.SlangSource;
import io.cloudslang.lang.entities.CompilationArtifact;
import io.cloudslang.lang.entities.SystemProperty;
import io.cloudslang.lang.systemtests.RuntimeInformation;
import io.cloudslang.lang.systemtests.StepData;
import io.cloudslang.lang.systemtests.ValueSyntaxParent;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * @author Bonczidai Levente
 * @since 11/6/2015
 */
public class FunctionDependenciesTest extends ValueSyntaxParent {

    private static final Set<SystemProperty> SYS_PROPS = new HashSet<>();
    @SuppressWarnings("unchecked")
    private static final Set<SystemProperty> EMPTY_SET = Collections.EMPTY_SET;
    static {
        SYS_PROPS.add(SystemProperty.createSystemProperty("user.sys", "props.host", "localhost"));
        SYS_PROPS.add(SystemProperty.createSystemProperty("user.sys", "props.port", "22"));
        SYS_PROPS.add(SystemProperty.createSystemProperty("user.sys", "props.alla", "balla"));
    }

    @Test
    public void testFunctionsBasic() throws Exception {
        URL resource = getClass().getResource("/yaml/functions/functions_test_flow.sl");
        URI operation = getClass().getResource("/yaml/functions/functions_test_op.sl").toURI();
        Set<SlangSource> path = Sets.newHashSet(SlangSource.fromFile(operation));
        CompilationArtifact compilationArtifact = slang.compile(SlangSource.fromFile(resource.toURI()), path);

        Map<String, Serializable> userInputs = prepareUserInputs();
        Set<SystemProperty> systemProperties = prepareSystemProperties();

        // trigger ExecutionPlan
        RuntimeInformation runtimeInformation = triggerWithData(compilationArtifact, userInputs, systemProperties);

        Map<String, StepData> executionData = runtimeInformation.getTasks();

        StepData flowData = executionData.get(EXEC_START_PATH);
        StepData taskData = executionData.get(FIRST_STEP_PATH);
        Assert.assertNotNull("flow data is null", flowData);
        Assert.assertNotNull("task data is null", taskData);

        verifyFlowInputs(flowData);
        verifyTaskArguments(taskData);
        verifyTaskPublishValues(taskData);

        // verify 'get' function worked in result expressions
        Assert.assertEquals("Function evaluation problem in result expression", "FUNCTIONS_KEY_EXISTS", flowData.getResult());
    }

    @Test
    public void testGetFunctionDefaultResult() throws Exception {
        URL resource = getClass().getResource("/yaml/functions/get_function_test_flow_default_result.sl");
        URI operation = getClass().getResource("/yaml/functions/get_function_test_default_result.sl").toURI();
        Set<SlangSource> path = Sets.newHashSet(SlangSource.fromFile(operation));
        CompilationArtifact compilationArtifact = slang.compile(SlangSource.fromFile(resource.toURI()), path);

        // trigger ExecutionPlan
        Map<String, Serializable> userInputs = new HashMap<>();
        RuntimeInformation runtimeInformation = triggerWithData(compilationArtifact, userInputs, EMPTY_SET);
        Map<String, StepData> executionData = runtimeInformation.getTasks();

        StepData flowData = executionData.get(EXEC_START_PATH);

        // verify 'get' function worked in result expressions
        Assert.assertEquals("Get function problem in result expression", "GET_FUNCTION_DEFAULT_VALUE", flowData.getResult());
    }

    private void verifyTaskArguments(StepData taskData) {
        // verify `get`, `get_sp()`, `locals().get()` and mixed mode works
        Map<String, Serializable> expectedArguments = new LinkedHashMap<>();
        expectedArguments.put("exist", "exist_value");
        expectedArguments.put("input_3", null);
        expectedArguments.put("input_4", "default_str");
        expectedArguments.put("input_5", "localhost");
        expectedArguments.put("input_6", "localhost");
        expectedArguments.put("input_7", "localhost");
        expectedArguments.put("input_8", "exist_value");
        expectedArguments.put("input_9", "localhost");
        expectedArguments.put("input_10", "localhost");
        Map<String, Serializable> actualArguments = taskData.getInputs();
        Assert.assertEquals("task arguments not as expected", expectedArguments, actualArguments);
    }

    private void verifyFlowInputs(StepData flowData) {
        // verify `get`, `get_sp()`, `locals().get()` and mixed mode works
        Map<String, Serializable> expectedFlowInputs = new LinkedHashMap<>();
        expectedFlowInputs.put("input1", null);
        expectedFlowInputs.put("input1_safe", "input1_default");
        expectedFlowInputs.put("input2", 22);
        expectedFlowInputs.put("input2_safe", 22);
        expectedFlowInputs.put("input_locals_found", 22);
        expectedFlowInputs.put("input_locals_not_found", "input_locals_not_found_default");
        expectedFlowInputs.put("exist", "exist_value");
        expectedFlowInputs.put("input_3", null);
        expectedFlowInputs.put("input_4", "default_str");
        expectedFlowInputs.put("input_5", "localhost");
        expectedFlowInputs.put("input_6", "localhost");
        expectedFlowInputs.put("input_7", "localhost");
        expectedFlowInputs.put("input_8", "exist_value");
        expectedFlowInputs.put("input_9", "localhost");
        expectedFlowInputs.put("input_10", "localhost");
        Map<String, Serializable> actualFlowInputs = flowData.getInputs();
        Assert.assertEquals("flow input values not as expected", expectedFlowInputs, actualFlowInputs);
    }

    private void verifyTaskPublishValues(StepData taskData) {
        // verify `get`, `get_sp()` and mixed mode works
        Map<String, Serializable> expectedOperationPublishValues = new LinkedHashMap<>();
        expectedOperationPublishValues.put("output1_safe", "CloudSlang");
        expectedOperationPublishValues.put("output2_safe", "output2_default");
        expectedOperationPublishValues.put("output_same_name", "output_same_name_default");
        expectedOperationPublishValues.put("output_1", null);
        expectedOperationPublishValues.put("output_2", "default_str");
        expectedOperationPublishValues.put("output_3", "localhost");
        expectedOperationPublishValues.put("output_4", "localhost");
        expectedOperationPublishValues.put("output_5", "localhost");
        expectedOperationPublishValues.put("output_6", "exist_value");
        expectedOperationPublishValues.put("output_7", "localhost");
        expectedOperationPublishValues.put("output_8", "localhost");
        Map<String, Serializable> actualOperationPublishValues = taskData.getOutputs();
        Assert.assertEquals("operation publish values not as expected", expectedOperationPublishValues, actualOperationPublishValues);
    }

    private Set<SystemProperty> prepareSystemProperties() {
        return Sets.newHashSet(
                SystemProperty.createSystemProperty("a.b", "c.host", "localhost"),
                SystemProperty.createSystemProperty("cloudslang", "lang.key", "language")
        );
    }

    private Map<String, Serializable> prepareUserInputs() {
        Map<String, Serializable> userInputs = new HashMap<>();
        userInputs.put("exist", "exist_value");
        return userInputs;
    }

}
