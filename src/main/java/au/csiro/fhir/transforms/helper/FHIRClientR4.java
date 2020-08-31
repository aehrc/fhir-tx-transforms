/*
 * Copyright © 2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.helper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent;

import java.util.LinkedList;

public class FHIRClientR4 {
	
	final int BATCH_SIZE = 40000;
	final int ZERO_SIZE = 0;

	FhirContext ctx;
	IGenericClient client;
	String sctVersion;
	
	public FHIRClientR4(String end) {
		ctx = FhirContext.forR4();
		client = ctx.newRestfulGenericClient(end);
		ctx.getRestfulClientFactory().setConnectTimeout(20 * 1000);
		ctx.getRestfulClientFactory().setSocketTimeout(800 * 1000);

	}
	
	
	/**
	 * Create code system if the resource with the ID is not in the server, Otherwise updated the code System 
	 * @param codeSystem
	 */
	public void createUpdateCodeSystem(CodeSystem codeSystem) {
			MethodOutcome outcome = client.update().resource(codeSystem).execute();
			System.out.println("Code System Updated, Got ID: " + outcome.getId().getValue());
	}
	
	/**
	 * Create value set if the resource with the ID is not in the server, Otherwise updated the value set 
	 * @param valueSet
	 */
	public void createUpdateValueSet(ValueSet valueSet) {
			MethodOutcome outcome = client.update().resource(valueSet).execute();
			System.out.println("ValueSet Updated, Got ID: " + outcome.getId().getValue());   
	}
	
	/**
	 * Create concept map if the resource with the ID is not in the server, Otherwise updated the concept map 
	 * @param conceptMap
	 */
	public void createUpdateMap(ConceptMap conceptMap) {
			MethodOutcome outcome = client.update().resource(conceptMap).execute();
			System.out.println("Concept Map Updated, Got ID: " + outcome.getId().getValue());
	}
	
	/**
	 * Value set Expand
	 * @param keyword
	 * @param count
	 * @param activeOnly
	 * @return
	 */
	public LinkedList<String> expandSCT(String keyword, int count, boolean activeOnly) {

		LinkedList<String> set = new LinkedList<>();
		Parameters parameters = new Parameters();
		ValueSet valueset = new ValueSet();
		ValueSetComposeComponent valueSetComposeComponent = new ValueSetComposeComponent();
		ConceptSetComponent conceptSetComponent = new ConceptSetComponent();
		conceptSetComponent.setSystem("http://snomed.info/sct");
		conceptSetComponent.setVersion("http://snomed.info/sct/32506021000036107/version/" + sctVersion );
		valueSetComposeComponent.addInclude(conceptSetComponent);
		valueset.setCompose(valueSetComposeComponent);

		ParametersParameterComponent parameterComponentValueset = new ParametersParameterComponent();
		parameterComponentValueset.setName("valueSet");
		parameterComponentValueset.setResource(valueset);
		parameters.addParameter(parameterComponentValueset);

		ParametersParameterComponent parameterComponentFilter = new ParametersParameterComponent();
		parameterComponentFilter.setName("filter");
		parameterComponentFilter.setValue(new StringType().setValue(keyword));
		parameters.addParameter(parameterComponentFilter);

		ParametersParameterComponent parameterComponentCount = new ParametersParameterComponent();
		parameterComponentCount.setName("count");
		parameterComponentCount.setValue(new IntegerType().setValue(count));
		parameters.addParameter(parameterComponentCount);

		ParametersParameterComponent parameterComponentActive = new ParametersParameterComponent();
		parameterComponentActive.setName("activeOnly");
		parameterComponentActive.setValue(new BooleanType().setValue(activeOnly));
		parameters.addParameter(parameterComponentActive);

		Parameters outParams = client.operation().onType(ValueSet.class).named("$expand").withParameters(parameters)
				.execute();
		ValueSet v = (ValueSet) outParams.getParameter().iterator().next().getResource();
		for (ValueSetExpansionContainsComponent c : v.getExpansion().getContains()) {

			set.add(c.getCode());
		}

		return set;
	}
	
	
	public void printResource(Resource r) {
		System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(r));
	}

	public String formatResource(Resource r) {
		return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(r);
	}
	
	
}
