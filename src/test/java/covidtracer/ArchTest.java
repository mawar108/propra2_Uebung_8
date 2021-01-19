package covidtracer;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import covidtracer.stereotypes.ClassOnly;
import covidtracer.stereotypes.Mutable;

import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static org.springframework.util.StringUtils.capitalize;

@AnalyzeClasses(packagesOf = CovidtracerApplication.class, importOptions = {ImportOption.DoNotIncludeTests.class})
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

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule allMethodsArePublic = methods()
			.that()
			.doNotHaveModifier(JavaModifier.SYNTHETIC)
			.and()
			.doNotHaveModifier(JavaModifier.BRIDGE)
			.should()
			.bePublic();

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule allConstructorsArePublic = constructors()
			.should()
			.bePublic();

	static ArchCondition<JavaMethod> notBeCalledByOtherClasses() {
		return new ArchCondition<>("methods annotated with @ClassOnly are not called by other classes") {
			@Override
			public void check(JavaMethod method, ConditionEvents events) {
				method.getCallsOfSelf()
						.stream()
						.filter(call -> !call.getOrigin().getOwner().equals(method.getOwner()))
						.forEach(call -> events.add(violated(method, "Method " + call.getName() + " cannot be called by other classes")));
			}
		};
	}

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule methodsThatAreAnnotatedWithClassOnlyAreNotCalledByOtherClasses = methods()
			.that()
			.areAnnotatedWith(ClassOnly.class)
			.should(notBeCalledByOtherClasses());

	static ArchCondition<JavaConstructor> constructorsNotBeCalledByOtherClasses() {
		return new ArchCondition<>("methods annotated with @ClassOnly are not called by other classes") {
			@Override
			public void check(JavaConstructor constructor, ConditionEvents events) {
				constructor.getCallsOfSelf()
						.stream()
						.filter(call -> !call.getOrigin().getOwner().equals(constructor.getOwner()))
						.forEach(call -> events.add(violated(constructor, "Constructor " + call.getName() + " cannot be called by other classes")));
			}
		};
	}

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule constructorsThatAreAnnotatedWithClassOnlyAreNotCalledByOtherClasses = constructors()
			.that()
			.areAnnotatedWith(ClassOnly.class)
			.should(constructorsNotBeCalledByOtherClasses());



	private static ArchCondition<JavaClass> haveNoSetters() {
		return new ArchCondition<JavaClass>("has no setter method for any field") {
			@Override
			public void check(final JavaClass javaClass, ConditionEvents events) {
				javaClass.getFields()
						.stream()
						.filter(f-> !f.isAnnotatedWith(Mutable.class))
						.forEach(f -> {
							try {
								var methodName = "set" + capitalize(f.getName());
								var parameterClass = Class.forName(f.getRawType().getFullName());
								var present = javaClass.tryGetMethod(methodName,parameterClass).isPresent();
								if (present) {
									events.add(SimpleConditionEvent.violated(f, "Field " + f.getFullName() + " has no Setter"));
								}
							}catch(Exception e){}
						});
			}
		};
	}


	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule noSetterInjectionUsed= classes()
			.should(haveNoSetters());

	@com.tngtech.archunit.junit.ArchTest
	static final ArchRule noFieldInjectionUsed= fields()
			.that()
			.areNotAnnotatedWith(Mutable.class)
			.should()
			.beFinal();




}
