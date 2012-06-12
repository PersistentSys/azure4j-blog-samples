
/*******************************************************************************
 * Copyright 2012 Persistent Systems Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.persistent.azure.jdbc.retry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * 
 * Class represent the JDBC Retry Policy. In case of transient failure, executes
 * the given task for the configured number of retries with waiting time between
 * retries
 * 
 */
public class RetryPolicy<T> implements Callable<T> {

    public static final int DEFAULT_RETRY_COUNT = 3;
    // In milliseconds
    public static final long DEFAULT_WAITING_TIME = 1000;
    // The number of retry attempts
    private int retryCount;
    // The number of retry attempt left
    private int retriesLeft;
    // The waiting time between retries
    private long waitingTime;
    // The task that will be executed
    private Callable<T> retriableTask;
    // List containing the error code to be considered as transient error
    private List<String> transientErrorCodeList = new ArrayList<String>();

    /**
     * Constructor
     * 
     * @param retriableTask
     *            The task that is to be retried in event of transient failure
     */
    public RetryPolicy(final Callable<T> retriableTask) {
        this(DEFAULT_RETRY_COUNT, DEFAULT_WAITING_TIME, retriableTask);
    }

    /**
     * This constructor populates the transient error code list and
     * initializes parameters
     * 
     * @param retryCount
     *            The number of times to retry the task
     * @param waitingTime
     *            The Time interval between tries
     * @param retriableTask
     *            The task that is to be retried in event of transient failure
     */
    public RetryPolicy(int retryCount, long waitingTime,
            final Callable<T> retriableTask) {
        populateErrorCodeList();
        this.retryCount = retryCount;
        this.retriesLeft = retryCount;
        this.waitingTime = waitingTime;
        this.retriableTask = retriableTask;
    }


    /**
     * Executes the task
     * 
     * @return The task computed result
     * @throws Exception
     */
    public T executetask() throws Exception {
        return call();
    }
    
    @Override
    public T call() throws Exception {
        while (true) {
            try {
                return retriableTask.call();
            } catch (SQLException sqlE) {
                try {
                    handleTransientException(sqlE);
                } catch (RetryPolicyException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }
    
    /**
     * Method to provide a pause between the retries. This is to provide some
     * recovery time for the cause of transient error
     */
    private void waitForRetryInterval() {
        try {
            Thread.sleep(waitingTime);
        } catch (InterruptedException e) {
        }
    }
    
    /**
     * Method to determine are there any retries left
     * 
     * @return true if retry iterations are left, false other wise
     */
    private Boolean isRetriesLeft() {
        if (retriesLeft > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Populates the transientErrorCodeList from
     * 'TransientErrorCodes.properties' file containing SQLAzure transient error
     * codes.
     * 
     */
    private void populateErrorCodeList() {
        File f = new File("src/TransientErrorCodes.properties");
        Properties pro = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(f);
            pro.load(in);
        } catch (IOException e) {}
        String errorCodes = pro.getProperty("transient_error_codes");
        String[] errorCodesArray = errorCodes.split(",");
        transientErrorCodeList.addAll(Arrays.asList(errorCodesArray));
    }
    
    /**
     * This method specifies the transient fault detection strategy
     * @param exception The Exception
     * @return true if Transient exception false otherwise
     */
    protected boolean isTransient(Exception exception) {
        boolean isTranisentException = false;
        String errorCode;
        if (exception instanceof SQLException) {
            errorCode = String.valueOf(((SQLException) exception)
                    .getErrorCode());
            if (transientErrorCodeList.contains(errorCode)) {
                isTranisentException = true;
            }
        }
        return isTranisentException;
    }

    /**
     * This method handles the the retry logic for transient exception. Checks
     * if retries left and the exception belongs to transient exception. if
     * exception is transient and retries left, then provides the 
     * waiting time between retries. Otherwise appropriate exception is thrown
     * 
     * @param sqlE
     *            The SQLException
     * @throws RetryPolicyException
     *             Thrown if no retries left
     * @throws SQLException
     *             Thrown if the SQL exception being handled is nonTransient
     */
    protected void handleTransientException(SQLException sqlE)
            throws RetryPolicyException, SQLException {

        // if no retries left throw exception
        if (!isRetriesLeft()) {
            throw new RetryPolicyException("Attempt to retry for "
                    + retryCount
                    + " times falied with " + waitingTime
                    + " millsecond interval");
        }
        // Decrement the retries count
        retriesLeft--;
        if (isTransient(sqlE)) {
            // Wait for specified time
            waitForRetryInterval();
        } else {
            // Its a Non transient exception Re throw the exception
            retriesLeft = 0;
            throw sqlE;
        }
    }
}