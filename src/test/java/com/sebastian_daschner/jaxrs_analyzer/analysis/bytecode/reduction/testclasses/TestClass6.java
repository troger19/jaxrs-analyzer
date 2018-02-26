/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

public class TestClass6 {

    public Response method(final int number) {
        synchronized (this) {
            return Response.status(3 * 2 / number).build();
        }
    }

    public static List<Instruction> instructions() {
        final List<Instruction> instructions = new LinkedList<>();

        instructions.add(new LoadInstruction(0, 'L' + TestClass6.class.getCanonicalName().replace('.', '/') + ';', "this", null, null));
        instructions.add(new DupInstruction(null));
        instructions.add(new StoreInstruction(2, Types.OBJECT, null));
        instructions.add(new SizeChangingInstruction("MONITORENTER", 0, 1, null));
        instructions.add(new LoadInstruction(2, Types.OBJECT, null, null));
        instructions.add(new SizeChangingInstruction("MONITOREXIT", 0, 1, null));
        instructions.add(new ExceptionHandlerInstruction(null));
        instructions.add(new StoreInstruction(3, Types.OBJECT, null));
        instructions.add(new PushInstruction(6, Types.PRIMITIVE_INT, null));
        instructions.add(new LoadInstruction(1, Types.PRIMITIVE_INT, "number", null, null));
        instructions.add(new SizeChangingInstruction("IDIV", 1, 2, null));
        instructions.add(new InvokeInstruction(MethodIdentifier.ofStatic(Types.CLASS_RESPONSE, "status", Types.RESPONSE_BUILDER, Types.PRIMITIVE_INT), null));
        instructions.add(new InvokeInstruction(MethodIdentifier.ofNonStatic(Types.CLASS_RESPONSE_BUILDER, "build", Types.RESPONSE), null));
        instructions.add(new LoadInstruction(2, Types.OBJECT, null, null));
        instructions.add(new SizeChangingInstruction("MONITOREXIT", 0, 1, null));
        instructions.add(new ReturnInstruction(null));
        instructions.add(new LoadInstruction(3, Types.OBJECT, null, null));
        instructions.add(new ThrowInstruction(null));

        return instructions;
    }

}
