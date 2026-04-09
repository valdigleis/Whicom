/**
 * MIT License
 * 
 * Copyright (c) 2026 Valdigleis S Costa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the right
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package site.valdigleis.whicom.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PreprocessorTest {

    @Test
    public void removeOneComentOnStartCode() {
        String input = "/* incrementa x */\nx := 1;\nx := 0;";
        String expected = "\nx := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeOneComentOnMiddleCode() {
        String input = "x := 1;\n/* incrementa x */\nx := 0;";
        String expected = "x := 1;\n\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeOneComentOnEndCode() {
        String input = "x := 1;\nx := 0;/* incrementa x */";
        String expected = "x := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeTwoComentsOnStartCode() {
        String input = "/* incrementa x *//* incrementa x */x := 1;\nx := 0;";
        String expected = "x := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeTwoComentsOnMiddleCode() {
        String input = "x := 1;\n/* incrementa x *//* incrementa x */\nx := 0;";
        String expected = "x := 1;\n\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeTwoComentsOnEndCode() {
        String input = "x := 1;\nx := 0;/* incrementa x *//* incrementa x */";
        String expected = "x := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeTwoComentsOnStartAndEndCode() {
        String input = "/* incrementa x */x := 1;\nx := 0;/* incrementa x */";
        String expected = "x := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeTwoComentsOnStartAndMiddleCode() {
        String input = "/* incrementa x */x := 1;/* incrementa x */\nx := 0;";
        String expected = "x := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void removeTwoComentsOnMiddleAndEndCode() {
        String input = "x := 1;/* incrementa x */\nx := 0;/* incrementa x */";
        String expected = "x := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

    @Test
    public void noRemoveComentsOnCode() {
        String input = "x := 1;\nx := 0;";
        String expected = "x := 1;\nx := 0;";
        assertEquals(expected, Preprocessor.removeComments(input));
    }

}
