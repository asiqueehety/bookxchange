package com.example.bookxchange;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.IncludeEngines;

@Suite
@IncludeEngines("junit-jupiter")
@SelectPackages("com.example.bookxchange.controller")
public class AllControllerTestsSuite {
}