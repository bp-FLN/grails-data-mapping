/* Copyright 2004-2005 the original author or authors.
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
package org.grails.orm.hibernate.support;

import groovy.lang.Closure;
import org.codehaus.groovy.grails.web.servlet.mvc.RedirectEventListener;
import org.grails.orm.hibernate.AbstractHibernateDatastore;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Flushes the session on a redirect.
 *
 * @author Graeme Rocher
 * @since 1.2
 */
public class FlushOnRedirectEventListener implements RedirectEventListener {

    private AbstractHibernateDatastore datastore;

    public FlushOnRedirectEventListener(AbstractHibernateDatastore datastore) {
        this.datastore = datastore;
    }

    public void responseRedirected(String url) {
        if( TransactionSynchronizationManager.hasResource( datastore.getSessionFactory() ) ) {
            datastore.getHibernateTemplate().execute(new Closure<Object>(this) {
                @Override
                public Object call(Object... args) {
                    Session session = (Session)args[0];
                    if (!FlushMode.isManualFlushMode(session.getFlushMode())) {
                        session.flush();
                    }
                    return null;
                }
            });
        }
    }
}
