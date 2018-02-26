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

package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import org.objectweb.asm.Label;

import java.util.Objects;

/**
 * Represents a NEW instruction.
 *
 * @author Sebastian Daschner
 */
public class NewInstruction extends Instruction {

    private final String className;

    public NewInstruction(final String className, final Label label) {
        super(label);
        Objects.requireNonNull(className);

        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public int getStackSizeDifference() {
        return 1;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.NEW;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NewInstruction that = (NewInstruction) o;

        return className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public String toString() {
        return "NewInstruction{" +
                "className='" + className + '\'' +
                '}';
    }

}
