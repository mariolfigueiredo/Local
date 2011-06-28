/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  http://geobatch.codehaus.org/
 *  Copyright (C) 2007-2008-2009 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 *
 */
package it.geosolutions.geobatch.ui.mvc;

import java.util.List;

import it.geosolutions.geobatch.flow.event.action.BaseAction;
import it.geosolutions.geobatch.flow.event.consumer.BaseEventConsumer;
import it.geosolutions.geobatch.flow.event.consumer.EventConsumerStatus;
import it.geosolutions.geobatch.flow.event.consumer.file.FileBasedEventConsumer;
import it.geosolutions.geobatch.flow.event.listeners.cumulator.CumulatingProgressListener;
import it.geosolutions.geobatch.flow.file.FileBasedFlowManager;

import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author ETj <etj at geo-solutions.it>
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class ConsumerDisposeController extends ConsumerAbstractController {

	@Override
	protected void runStuff(ModelAndView mav, FileBasedFlowManager fm,
			BaseEventConsumer consumer) {
		if (fm != null && consumer != null) {
			final EventConsumerStatus status = consumer.getStatus();
			
			if (status.equals(EventConsumerStatus.COMPLETED)
					|| status.equals(EventConsumerStatus.CANCELED)
					|| status.equals(EventConsumerStatus.FAILED)) {
				
				// Progress Logging...
				CumulatingProgressListener cpl;

				cpl = (CumulatingProgressListener) consumer
						.getProgressListener(CumulatingProgressListener.class);
				if (cpl != null)
					cpl.clearMessages();

				// Current Action Status...
				final List<BaseAction> actions = consumer.getActions();
				if (actions != null) {
					for (BaseAction action : actions) {
						// try the most interesting information holder
						cpl = (CumulatingProgressListener) action
								.getProgressListener(CumulatingProgressListener.class);
						if (cpl != null)
							cpl.clearMessages();
					}
				}
				fm.dispose((FileBasedEventConsumer) consumer);
			}
		}

		mav.addObject("consumer", consumer);
	}
}