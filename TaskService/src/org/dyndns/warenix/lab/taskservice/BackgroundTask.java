package org.dyndns.warenix.lab.taskservice;

import java.io.Serializable;

public interface BackgroundTask extends Serializable {

	public Object onExecute() throws Exception;

	public Object getResult();

}
