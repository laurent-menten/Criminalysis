/*
 * ============================================================================
 * =- Criminalysis -=- A crime analysis toolbox -=- (c) 2024+ Laurent Menten -=
 * ============================================================================
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 * ============================================================================
 * jDungeonMaster is unofficial Fan Content permitted under the Fan Content
 * Policy. Not approved/endorsed by Wizards. Portions of the materials used
 * are property of Wizards of the Coast. ©Wizards of the Coast LLC.
 * See <https://company.wizards.com/en/legal/fancontentpolicy> for details.
 * ============================================================================
 */

package be.lmenten.annotation.processor;

import be.lmenten.annotation.TestAnnotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Set;

@SupportedSourceVersion( SourceVersion.RELEASE_21 )
@SupportedAnnotationTypes( "be.lmenten.*" )
public class TestAnnotationProcessor
	extends AbstractProcessor
{
	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public TestAnnotationProcessor()
	{
	}

	// ========================================================================
	// =
	// ========================================================================

	@Override
	public synchronized void init( ProcessingEnvironment processingEnv )
	{
		super.init( processingEnv );

		messager = processingEnv.getMessager();

		filer = processingEnv.getFiler();
		typeUtils = processingEnv.getTypeUtils();
		elementUtils = processingEnv.getElementUtils();

		Map<String,String> options = processingEnv.getOptions();
		for( String key : options.keySet() )
		{
			String value = options.get( key );
		}
	}

	// ========================================================================
	// =
	// ========================================================================

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
	{
		messager.printMessage( Diagnostic.Kind.NOTE, "Processing..." );

		for( Element annotatedElement : roundEnv.getElementsAnnotatedWith( TestAnnotation.class ) )
		{
			messager.printMessage( Diagnostic.Kind.WARNING, annotatedElement.toString() );
		}

		return true;
	}
}
