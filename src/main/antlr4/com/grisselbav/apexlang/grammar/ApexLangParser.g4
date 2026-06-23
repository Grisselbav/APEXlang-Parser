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
    component* ws? EOF
;

/*----------------------------------------------------------------------------*/
// component
/*----------------------------------------------------------------------------*/

component:
    ws? type=ID (ws name=key)? body=componentBody
;

componentBody:
    ws? LPAR items+=componentBodyItem* ws? RPAR
;

componentBodyItem:
      component
    | group
    | property
;

/*----------------------------------------------------------------------------*/
// group
/*----------------------------------------------------------------------------*/

group:
    ws? name=ID body=groupBody
;

groupBody:
    ws? LCUB items+=componentBodyItem* ws? RCUB
;

/*----------------------------------------------------------------------------*/
// property
/*----------------------------------------------------------------------------*/

property:
    wsWithNewLine name=key ws? COLON ws pvalue=value
;

value:
      componentBody
    | groupBody
    | reference
    | array
    | ML_STRING
    | propertyValue // contains SL_STRING as propertyValueStart
;

/*----------------------------------------------------------------------------*/
// key
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

/*----------------------------------------------------------------------------*/
// property value
/*----------------------------------------------------------------------------*/

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
    | HWS
;

propertyValue:
    propertyValueStart propertyValueCont*
;

/*----------------------------------------------------------------------------*/
// reference
/*----------------------------------------------------------------------------*/

reference:
    COMMAT name=key
;

/*----------------------------------------------------------------------------*/
// array
/*----------------------------------------------------------------------------*/

array:
    LSQB ws? (entries+=arrayEntry (ws entries+=arrayEntry)* ws?)? RSQB
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
    (HWS | NL)+
;

wsWithNewLine:
    HWS* NL (HWS | NL)*
;
