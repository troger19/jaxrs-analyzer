package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Sebastian Daschner
 */
abstract class ValueAnnotationVisitor extends AnnotationVisitor {

    private static final String NAME = "value";

    ValueAnnotationVisitor() {
        super(Opcodes.ASM5);
    }

    protected abstract void visitValue(String value);

    @Override
    public void visit(String name, Object value) {
        if (NAME.equals(name)) {
            visitValue((String) value);
        }
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return new AnnotationVisitor(Opcodes.ASM5) {
            @Override
            public void visit(String name, Object value) {
                visitValue((String) value);
            }
        };
    }
}
