/*
 * Copyright (C) 2016 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.nsym.domain.model.repository.tools;

import br.com.nsym.domain.model.repository.IGenericRepository;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.tools.Message;
import br.com.nsym.domain.model.security.User;

/**
 * 
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 2.2.0, 24/02/2016
 */
public interface IMessageRepository extends IGenericRepository<Message, Long> {
    
    /**
     * 
     * @param sender
     * @param filter
     * @param pageRequest
     * @return 
     */
    Page<Message> listSent(User sender, String filter, PageRequest pageRequest);
}
