/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.domain;

import org.apache.commons.lang.builder.CompareToBuilder;

import java.io.Serializable;

public abstract class AbstractPersistable implements Serializable, Comparable<AbstractPersistable> {

    protected Long id;

    protected AbstractPersistable() {
    }

    protected AbstractPersistable(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int compareTo(AbstractPersistable other) {
        return new CompareToBuilder()
                .append(getClass().getName(), other.getClass().getName())
                .append(id, other.id)
                .toComparison();
    }

    public String toString() {
        return "[" + getClass().getName().replaceAll(".*\\.", "") + "-" + id + "]";
    }

}
