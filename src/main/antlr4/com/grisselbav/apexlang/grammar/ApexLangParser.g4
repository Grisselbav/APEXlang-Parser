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

parser grammar ApexLangParser;

options {
    tokenVocab=ApexLangLexer;
    contextSuperClass=ApexLangParserRuleContext;
}

/*----------------------------------------------------------------------------*/
// Start rule
/*----------------------------------------------------------------------------*/

apxFile:
    component* ws* EOF
;

/*----------------------------------------------------------------------------*/
// component
/*----------------------------------------------------------------------------*/

component:
    ws* type=ID (ws+ name=key)? body=componentBody
;

componentBody:
    ws* LPAR items+=componentBodyItem* ws* RPAR
;

componentBodyItem:
      component
    | group
    | property
;

group:
    ws* name=ID body=groupBody
;

groupBody:
    ws* LCUB items+=componentBodyItem* ws* RCUB
;

property:
    NL+ ws* name=key ws* COLON ws+ pvalue=value
;

/*----------------------------------------------------------------------------*/
// data type rules
/*----------------------------------------------------------------------------*/

keyStart:
      ID
    | ANY_OTHER
;

keyCont:
      keyStart
    | COMMAT
    | COLON
;

key:
      keyStart keyCont*     # unquotedKey
    | SL_STRING             # quotedKey
;

propertyValueStart:
      ID
    | LPAR
    | RPAR
    | ANY_OTHER
    | COLON
    | LCUB
    | RCUB
    | LSQB
    | RSQB
    | SL_STRING
;

propertyValueCont:
      propertyValueStart
    | COMMAT
    | WS
;

propertyValue:
    propertyValueStart propertyValueCont*
;

value:
      componentBody
    | groupBody
    | reference
    | array
    | SL_STRING
    | ML_STRING
    | propertyValue
;

reference:
    COMMAT name=key
;

array:
    LSQB ws* (entries+=arrayEntry (ws+ entries+=arrayEntry)* ws*)? RSQB
;

arrayEntry:
      reference
    | key           // contains SL_STRING as quotedKey
    | array
    | ML_STRING
;

/*----------------------------------------------------------------------------*/
// whitespace
/*----------------------------------------------------------------------------*/

ws:
    (WS | NL)
;