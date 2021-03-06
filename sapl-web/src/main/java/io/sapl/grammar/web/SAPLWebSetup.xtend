/*
 * SAPL generated by Xtext
 */
package io.sapl.grammar.web

import com.google.inject.Guice
import com.google.inject.Injector
import io.sapl.grammar.SAPLRuntimeModule
import io.sapl.grammar.SAPLStandaloneSetup
import io.sapl.grammar.ide.SAPLIdeModule
import org.eclipse.xtext.util.Modules2

/**
 * Initialization support for running Xtext languages in web applications.
 */
class SAPLWebSetup extends SAPLStandaloneSetup {
	
	override Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new SAPLRuntimeModule, new SAPLIdeModule, new SAPLWebModule))
	}
	
}
