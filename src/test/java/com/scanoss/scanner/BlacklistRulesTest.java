package com.scanoss.scanner;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BlacklistRulesTest {

	@Test
	public void isMarkupOrJSON_true_if_JSON()
	{
		String testJsonString = "{}";
		assertTrue(BlacklistRules.isMarkupOrJSON(testJsonString));
	}
}
