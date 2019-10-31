/*
 * Copyright 2019 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.looseboxes.msofficekiosk.misc;

import org.eclipse.swt.SWT;
import static org.eclipse.swt.SWT.Help;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.ole.win32.TYPEATTR;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.OleFunctionDescription;
import org.eclipse.swt.ole.win32.OlePropertyDescription;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2019 11:50:07 AM
 */
public class PrintPropertiesAndMethodsSupportedByOLEDocumentOrActiveXControl {

    public static void main(String[] args) {
//    if (args.length == 0) {
//    System.out.println("Usage: java Main ");
//    return;
//   }
    args = new String[]{"Word.Document"};
    String progID = args[0];

    Shell shell = new Shell();

    OleFrame frame = new OleFrame(shell, SWT.NONE);
    OleControlSite site = null;
    OleAutomation auto = null;
    try {
    site = new OleControlSite(frame, SWT.NONE, progID);
    auto = new OleAutomation(site);
    } catch (SWTException ex) {
    System.out.println("Unable to open type library for " + progID);
    return;
    }

    TYPEATTR typeattr = auto.getTypeInfoAttributes();
    if (typeattr != null) {
    if (typeattr.cFuncs > 0)
    System.out.println("Functions for " + progID + " :");
    for (int i = 0; i < typeattr.cFuncs; i++) {
    OleFunctionDescription data = auto.getFunctionDescription(i);
    String argList = "";
    int firstOptionalArgIndex =
    data.args.length - data.optionalArgCount;
    for (int j = 0; j < data.args.length; j++) {
    argList += "[";
    if (j >= firstOptionalArgIndex)
    argList += "optional, ";
    argList += getDirection(data.args[j].flags)
    + "] "
    + getTypeName(data.args[j].type)
    + " "
    + data.args[j].name;
    if (j < data.args.length - 1)
    argList += ", ";
    }
    System.out.println(
    getInvokeKind(data.invokeKind)
    + " (id = "
    + data.id
    + ") : "
    + " Signature : "
    + getTypeName(data.returnType)
    + " "
    + data.name
    + "("
    + argList
    + ")"
    + " Description : "
    + data.documentation
    + " Help File : "
    + data.helpFile
    + " ");
    }

    if (typeattr.cVars > 0)
    System.out.println("Variables for " + progID + " :   ");
    for (int i = 0; i < typeattr.cVars; i++) {
    OlePropertyDescription data = auto.getPropertyDescription(i);
    System.out.println(
    "PROPERTY (id = "
    + data.id
    + ") :"
    + "Name : "
    + data.name
    + "Type : "
    + getTypeName(data.type)
    + "");
    }
    }

    auto.dispose();
    shell.dispose();
    }

    private static String getTypeName(int type) {
        switch (type) {
            case OLE.VT_BOOL :
            return "boolean";
            case OLE.VT_R4 :
            return "float";
            case OLE.VT_R8 :
            return "double";
            case OLE.VT_I4 :
            return "int";
            case OLE.VT_DISPATCH :
            return "IDispatch";
            case OLE.VT_UNKNOWN :
            return "IUnknown";
            case OLE.VT_I2 :
            return "short";
            case OLE.VT_BSTR :
            return "String";
            case OLE.VT_VARIANT :
            return "Variant";
            case OLE.VT_CY :
            return "Currency";
            case OLE.VT_DATE :
            return "Date";
            case OLE.VT_UI1 :
            return "unsigned char";
            case OLE.VT_UI4 :
            return "unsigned int";
            case OLE.VT_USERDEFINED :
            return "UserDefined";
            case OLE.VT_HRESULT :
            return "int";
            case OLE.VT_VOID :
            return "void";

            case OLE.VT_BYREF | OLE.VT_BOOL :
            return "boolean *";
            case OLE.VT_BYREF | OLE.VT_R4 :
            return "float *";
            case OLE.VT_BYREF | OLE.VT_R8 :
            return "double *";
            case OLE.VT_BYREF | OLE.VT_I4 :
            return "int *";
            case OLE.VT_BYREF | OLE.VT_DISPATCH :
            return "IDispatch *";
            case OLE.VT_BYREF | OLE.VT_UNKNOWN :
            return "IUnknown *";
            case OLE.VT_BYREF | OLE.VT_I2 :
            return "short *";
            case OLE.VT_BYREF | OLE.VT_BSTR :
            return "String *";
            case OLE.VT_BYREF | OLE.VT_VARIANT :
            return "Variant *";
            case OLE.VT_BYREF | OLE.VT_CY :
            return "Currency *";
            case OLE.VT_BYREF | OLE.VT_DATE :
            return "Date *";
            case OLE.VT_BYREF | OLE.VT_UI1 :
            return "unsigned char *";
            case OLE.VT_BYREF | OLE.VT_UI4 :
            return "unsigned int *";
            case OLE.VT_BYREF | OLE.VT_USERDEFINED :
            return "UserDefined *";
            default:
            return "unknown " + type;    
        }
    }

    private static String getDirection(int direction) {
        String dirString = "";
        boolean comma = false;
        if ((direction & OLE.IDLFLAG_FIN) != 0) {
            dirString += "in";
            comma = true;
        }
        if ((direction & OLE.IDLFLAG_FOUT) != 0) {
            if (comma)
            dirString += ", ";
            dirString += "out";
            comma = true;
        }
        if ((direction & OLE.IDLFLAG_FLCID) != 0) {
            if (comma)
            dirString += ", ";
            dirString += "lcid";
            comma = true;
        }
        if ((direction & OLE.IDLFLAG_FRETVAL) != 0) {
            if (comma)
            dirString += ", ";
            dirString += "retval";
        }

        return dirString;
    }

    private static String getInvokeKind(int invKind) {
        switch (invKind) {
            case OLE.INVOKE_FUNC :
                return "METHOD";
            case OLE.INVOKE_PROPERTYGET :
                return "PROPERTY GET";
            case OLE.INVOKE_PROPERTYPUT :
                return "PROPERTY PUT";
            case OLE.INVOKE_PROPERTYPUTREF :
                return "PROPERTY PUT BY REF";
            default:    
                return "unknown " + invKind;
        }
    }
}
