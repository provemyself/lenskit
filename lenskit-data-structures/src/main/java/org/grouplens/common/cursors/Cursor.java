/*
 * GroupLens Common Utilities
 * Copyright © 2011 Regents of the University of Minnesota
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the
 *    distribution.
 *
 *  * Neither the name of the University of Minnesota nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software has been partly funded by NSF grant IIS 08-08692.
 */
package org.grouplens.common.cursors;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

/**
 * Cursors over data connections.  These are basically closable iterators which
 * also implement {@link Iterable} for convenience.  Cursors are not allowed to
 * contain null elements.
 * 
 * <p>
 * Note that the {@link #iterator()} method does <b>not</b> return a fresh
 * iterator but rather a wrapper of this cursor; it is only present to allow
 * for-each loops over cursors.  After it is exhausted, any iterator returned
 * will be null.
 * 
 * <p>
 * This class does not implement {@link Iterator} because the 'is-a' relationship
 * does not hold; cursors must be closed by their clients while iterators do
 * not have such a requirement.
 * 
 * @author Michael Ekstrand <ekstrand@cs.umn.edu>
 *
 * @param <T> The type of data returned by the cursor
 */
public interface Cursor<T> extends Iterable<T>, Closeable {
	/**
	 * Get an upper bound on the number of rows available from the cursor.
	 * @return the maximum number of rows which may be returned by {@link #next()},
	 * or -1 if that count is not available.
	 */
	int getRowCount();

	/**
	 * Query whether the cursor has any more items.  If the cursor or underlying
	 * source has been closed, this may return even if the end has not been
	 * reached.
	 * @return <tt>true</tt> if there remains another item to fetch.
	 */
	boolean hasNext();
	
	/**
	 * Fetch the next item from the cursor.
	 * @return The next item in the cursor.
	 * @throws NoSuchElementException if there are no more elements.
	 * @throws RuntimeException if the cursor or its data source have been
	 * closed (optional).
	 */
	@Nonnull T next();
	
	/**
     * Variant of {@link #next()} that may mutate and return the same object
     * avoid excess allocations. Since many loops don't do anything with the
     * object after the iteration in which it is retrieved, using this iteration
     * method can improve both speed and memory use.
     * @see Cursor#next()  
     */
	@Nonnull T fastNext();
	
	/**
	 * Convert the cursor to an iterable whose {@link Iterator#next()} method is
	 * implemented in terms of {@link #fastNext()}.
	 * @return An iterable for fast iteration.
	 */
	Iterable<T> fast();
	
	/**
	 * Close the cursor.  This invalidates the cursor; no more elements may be
	 * fetched after a call to <tt>close()</tt> (although implementations are
	 * not required to enforce this).  It is not an error to close a cursor
	 * multiple times.
	 */
	@Override
    void close();
}