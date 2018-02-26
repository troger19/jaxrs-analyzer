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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;

import java.util.LinkedList;
import java.util.List;

public class TestClass6 {

    public synchronized int method(final int number) {
        return 3 * 2 / number;
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new PushInstruction(6, Types.PRIMITIVE_INT, null));
        instructions.add(new LoadInstruction(1, Types.PRIMITIVE_INT, "number", null, null));
        instructions.add(new SizeChangingInstruction("IDIV", 1, 2, null));
        instructions.add(new ReturnInstruction(null));

        return instructions;
    }

}
