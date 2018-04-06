package com.ibm.decisions.spark;

import ilog.rules.res.session.ruleset.IlrExecutionTrace;
import ilog.rules.res.session.ruleset.IlrRuleInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scala.Tuple2;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DecisionTrace {
	
	@JsonIgnore public IlrExecutionTrace executionTrace;
	@JsonIgnore private List<Tuple2<IlrRuleInformation, Long>> ruleTuples = null;
	private ArrayList<DecisionCoverage> ruleCoverages;
	
	//public HashMap<String, Long> ruleCoverageMap = new HashMap<String, Long>();
	
	public DecisionTrace() {
		
	}
	
	public DecisionTrace(IlrExecutionTrace executionTrace) {
		this.setExecutionTrace(executionTrace);
	}

	@JsonIgnore public void setExecutionTrace(IlrExecutionTrace executionTrace) {
		this.executionTrace = executionTrace;
		
		//Derived field
		computeRuleCountTuples();
	}

	@JsonIgnore public IlrExecutionTrace getExecutionTrace() {
		return executionTrace;
	}

	@JsonIgnore public void setRuleCountTuples(List<Tuple2<IlrRuleInformation, Long>> ruleCountTuples) {
		this.ruleTuples = ruleCountTuples;
	}
	
	//ToDo Handle when a rule is fired several times
	@JsonIgnore public List<Tuple2<IlrRuleInformation, Long>> getRuleCountTuples() {
		if (ruleTuples == null) {
			computeRuleCountTuples();
		}
		return ruleTuples;
	}
	
	//ToDo Handle when a rule is fired several times
	public List<DecisionCoverage> getRuleCoverages() {
		if (ruleCoverages == null) {
			computeRuleCountTuples();
		}
		return ruleCoverages;
	}
	
	@JsonIgnore private void computeRuleCountTuples() {
		ruleTuples = new ArrayList<Tuple2<IlrRuleInformation, Long>>(); 
		ruleCoverages = new ArrayList<DecisionCoverage>(); //@ToDo remove duplication of array
		
		// Rules not fired
		Set<IlrRuleInformation> notExecutedRules = getExecutionTrace().getRulesNotFired();
		Iterator<IlrRuleInformation> itNotExecutedRule = notExecutedRules.iterator();
		while (itNotExecutedRule.hasNext()) {
			IlrRuleInformation ruleInfo = itNotExecutedRule.next();
			ruleTuples.add(new Tuple2<IlrRuleInformation, Long>(ruleInfo, (long) 0));
			ruleCoverages.add(new DecisionCoverage(ruleInfo, (long) 0));
		}

		// Rules fired
		Map<String, IlrRuleInformation> allRuleMap = getExecutionTrace().getRules();
		Collection<IlrRuleInformation> allRules = allRuleMap.values();
		Iterator<IlrRuleInformation> ruleInfoIterator = allRules.iterator();
		while (ruleInfoIterator.hasNext()) {
			IlrRuleInformation ruleInfo = ruleInfoIterator.next();
			if (!notExecutedRules.contains(ruleInfo)) {
				ruleTuples.add(new Tuple2<IlrRuleInformation, Long>(ruleInfo, (long) 1));
				ruleCoverages.add(new DecisionCoverage(ruleInfo, (long) 1));
			}
		}
	}
	
}
