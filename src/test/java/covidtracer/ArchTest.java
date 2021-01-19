package covidtracer;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import covidtracer.stereotypes.Mutable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packagesOf = CovidtracerApplication.class)
public class ArchTest {

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule noClassIsDeprecated= noClasses()
			.should()
			.beAnnotatedWith(Deprecated.class);

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule noMethodIsDeprecated= noMethods()
			.should()
			.beAnnotatedWith(Deprecated.class);

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule allNonFinalFieldsAreMutable = fields()
			.that()
			.areNotFinal()
			.should()
			.beAnnotatedWith(Mutable.class);


}
