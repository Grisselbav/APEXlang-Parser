/*
 * Copyright 2026 Philipp Salvisberg <philipp.salvisberg@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

lexer grammar ApexLangLexer;

/*----------------------------------------------------------------------------*/
// Whitespace (visible, parser needs to handle all occurrences)
/*----------------------------------------------------------------------------*/

NL: '\r'? '\n';
BLANK: [ \t]+;

/*----------------------------------------------------------------------------*/
// Comments
/*----------------------------------------------------------------------------*/

SL_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
ML_COMMENT: '/*' .*? '*/' -> channel(HIDDEN);

/*----------------------------------------------------------------------------*/
// Identifier (matches also numbers)
/*----------------------------------------------------------------------------*/

fragment ID_START: [A-Za-z0-9_];
fragment ID_CONT: [A-Za-z0-9_.-];
ID: ID_START ID_CONT*;

/*----------------------------------------------------------------------------*/
// Strings
/*----------------------------------------------------------------------------*/

fragment STRING_CHARACTER: ~["]; // not defined in apexlang.ebnf
fragment SL_ESCAPE: '\\"';
SL_STRING: '"' (SL_ESCAPE | STRING_CHARACTER)* '"';
fragment ML_ESCAPE: '\\```';
ML_STRING: '```' (ML_ESCAPE | .)*? '```'; // language indicator such as sql, plsql, js is part of the string

/*----------------------------------------------------------------------------*/
// Special characters - naming according HTML entity name
/*----------------------------------------------------------------------------*/

// see https://html.spec.whatwg.org/multipage/named-characters.html#named-character-references
// or https://oinam.github.io/entities/

COLON: ':';
COMMAT: '@';
LCUB: '{';
LPAR: '(';
LSQB: '[';
RCUB: '}';
RPAR: ')';
RSQB: ']';

/*----------------------------------------------------------------------------*/
// Any other token
/*----------------------------------------------------------------------------*/

ANY_OTHER: .;
