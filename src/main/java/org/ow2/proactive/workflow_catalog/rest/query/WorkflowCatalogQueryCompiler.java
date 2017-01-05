/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.workflow_catalog.rest.query;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.ow2.proactive.workflow_catalog.rest.query.parser.WorkflowCatalogQueryLanguageLexer;
import org.ow2.proactive.workflow_catalog.rest.query.parser.WorkflowCatalogQueryLanguageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Compile a Workflow Catalog query as input String to an Abstract Syntax Tree (AST).
 *
 * @author ActiveEon Team
 */
public class WorkflowCatalogQueryCompiler {

    private static final Logger log = LoggerFactory.getLogger(WorkflowCatalogQueryCompiler.class);

    /**
     * Compile the specified Workflow Catalog to an AST.
     *
     * @param input the Workflow Catalog query to compile.
     * @return the root of the AST.
     * @throws SyntaxException if one or more syntax errors are detected while compiling.
     */
    public WorkflowCatalogQueryLanguageParser.StartContext compile(String input) throws SyntaxException {
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();

        // lexer splits input into tokens
        ANTLRInputStream antlrStringStream = new ANTLRInputStream(input);

        WorkflowCatalogQueryLanguageLexer workflowCatalogQueryLanguageLexer = new WorkflowCatalogQueryLanguageLexer(antlrStringStream);
        workflowCatalogQueryLanguageLexer.removeErrorListeners();
        workflowCatalogQueryLanguageLexer.addErrorListener(syntaxErrorListener);

        // create a buffer of tokens pulled from the lexer
        TokenStream tokens = new CommonTokenStream(workflowCatalogQueryLanguageLexer);

        // parser generates abstract syntax tree
        WorkflowCatalogQueryLanguageParser parser = new WorkflowCatalogQueryLanguageParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorListener);

        // begin parsing at root rule
        WorkflowCatalogQueryLanguageParser.StartContext expression = parser.start();

        if (log.isDebugEnabled()) {
            log.debug("WCQL Tree:\n" + expression.toStringTree(parser));
        }

        if (!syntaxErrorListener.getSyntaxErrors().isEmpty()) {
            throw new SyntaxException(syntaxErrorListener.getSyntaxErrors());
        }

        return expression;
    }

}
