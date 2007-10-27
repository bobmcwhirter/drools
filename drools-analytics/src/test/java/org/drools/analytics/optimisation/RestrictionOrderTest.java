package org.drools.analytics.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.StatelessSession;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.AnalyticsComponent;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.OperatorDescr;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.components.Restriction;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Cause;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class RestrictionOrderTest extends TestBase {

	public void testRestrictionOrderInsideOperator() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("RestrictionOrder.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Optimise restrictions inside operator"));

		// Clear data so that test data doesn't mix.
		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("OptimisationRestrictionOrderTest.drl"));

		// Clear result so that test data doesn't mix.
		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.NOTE).iterator();

		Collection<String> ruleNames = new ArrayList<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				String name = ((AnalyticsMessage) o).getCauses().toArray(
						new Restriction[2])[0].getRuleName();

				ruleNames.add(name);
			}
		}

		assertTrue(ruleNames.remove("Wrong descr order 1"));

		if (!ruleNames.isEmpty()) {
			for (String string : ruleNames) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testRestrictionOrderInsideConstraint() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("RestrictionOrder.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Optimise restrictions inside constraint"));

		Collection<Object> testData = new ArrayList<Object>();

		/*
		 * Case 1
		 */
		Pattern pattern1 = new Pattern();
		testData.add(pattern1);

		OperatorDescr parent1 = new OperatorDescr(OperatorDescr.Type.AND);

		LiteralRestriction r1 = new LiteralRestriction();
		r1.setPatternId(pattern1.getId());
		r1.setParent(parent1);
		r1.setEvaluator(">");
		r1.setOrderNumber(1);
		testData.add(r1);

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setPatternId(pattern1.getId());
		r2.setParent(parent1);
		r2.setEvaluator("==");
		r2.setOrderNumber(2);
		testData.add(r2);

		LiteralRestriction r3 = new LiteralRestriction();
		r3.setPatternId(pattern1.getId());
		r3.setParent(parent1);
		r3.setEvaluator("<");
		r3.setOrderNumber(3);
		testData.add(r3);

		/*
		 * Case 2
		 */
		Pattern pattern2 = new Pattern();
		testData.add(pattern2);

		OperatorDescr parent2 = new OperatorDescr(OperatorDescr.Type.OR);

		LiteralRestriction r4 = new LiteralRestriction();
		r4.setPatternId(pattern2.getId());
		r4.setParent(parent2);
		r4.setEvaluator("!=");
		r4.setOrderNumber(1);
		testData.add(r4);

		LiteralRestriction r5 = new LiteralRestriction();
		r5.setPatternId(pattern2.getId());
		r5.setParent(parent2);
		r5.setEvaluator("<=");
		r5.setOrderNumber(2);
		testData.add(r5);

		LiteralRestriction r6 = new LiteralRestriction();
		r6.setPatternId(pattern2.getId());
		r6.setParent(parent2);
		r6.setEvaluator("!=");
		r6.setOrderNumber(3);
		testData.add(r6);

		// Clear result so that test data doesn't mix.
		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.NOTE).iterator();

		Map<Cause, Cause> pairs = new HashMap<Cause, Cause>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Cause left = ((AnalyticsMessage) o).getCauses().toArray(
						new Cause[2])[0];
				Cause right = ((AnalyticsMessage) o).getCauses().toArray(
						new Cause[2])[1];

				pairs.put(left, right);
			}
		}

		// Check that case 1 is here.
		assertTrue((pairs.containsKey(r1) && pairs.get(r1).equals(r2))
				|| pairs.containsKey(r2) && pairs.get(r2).equals(r1));

		// Check that case 2 is here.
		assertTrue((pairs.containsKey(r4) && pairs.get(r4).equals(r5))
				|| pairs.containsKey(r5) && pairs.get(r5).equals(r4));

		// Check that there is only one pair.
		assertEquals(2, pairs.size());
	}

	public void testEvalOrderInsideOperator() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("RestrictionOrder.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Optimise predicates inside operator"));

		// Clear data so that test data doesn't mix.
		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("OptimisationRestrictionOrderTest.drl"));

		// Clear result so that test data doesn't mix.
		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.NOTE).iterator();

		Collection<String> ruleNames = new ArrayList<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				String name = ((AnalyticsMessage) o).getCauses().toArray(
						new AnalyticsComponent[2])[0].getRuleName();

				ruleNames.add(name);
			}
		}

		assertTrue(ruleNames.remove("Wrong eval order 1"));

		if (!ruleNames.isEmpty()) {
			for (String string : ruleNames) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
