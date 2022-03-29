/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.tls;

import com.oracle.bmc.OCID;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.coherence.common.base.Logger;

import com.oracle.coherence.oci.secret.util.SecretsFetcher;

import com.tangosol.net.ssl.AbstractPrivateKeyLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link com.tangosol.net.ssl.PrivateKeyLoader} that loads a key
 * from an OCI secret.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsPrivateKeyLoader
        extends AbstractPrivateKeyLoader
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link SecretsPrivateKeyLoader}.
     *
     * @param auth              the {@link AbstractAuthenticationDetailsProvider OCI authentication} to use
     * @param sSecret           a secret OCID or name
     * @param sCompartmentOCID  an optional OCI compartment OCID if the secret is a name instead of an OCID
     */
    public SecretsPrivateKeyLoader(AbstractAuthenticationDetailsProvider auth, String sSecret, String sCompartmentOCID)
        {
        super(sSecret);
        f_fetcher          = new SecretsFetcher(auth);
        f_sSecret          = sSecret;
        f_sCompartmentOCID = sCompartmentOCID;
        }

    // ----- AbstractPrivateKeyLoader methods -------------------------------

    @Override
    protected InputStream getInputStream() throws IOException
        {
        Logger.finest("Loading private key from " + this);

        byte[] abData;

        if (OCID.isValid(f_sSecret))
            {
            abData = f_fetcher.get(f_sSecret);
            }
        else
            {
            abData = f_fetcher.get(f_sSecret, f_sCompartmentOCID);
            }

        return abData == null ? null : new ByteArrayInputStream(abData);
        }

    // ----- Object methods -------------------------------------------------

    @Override
    public String toString()
        {
        if (f_sCompartmentOCID == null)
            {
            return "OCISecretKey(" + f_sSecret + "}";
            }
        return "OCISecretKey(" +
                "secret='" + f_sSecret + '\'' +
                ", compartment='" + f_sCompartmentOCID + "'}";
        }

    // ----- data members ---------------------------------------------------

    /**
     * The Secret Service client.
     */
    private final SecretsFetcher f_fetcher;

    /**
     * A secret OCID or name.
     */
    private final String f_sSecret;

    /**
     * An optional OCI compartment OCID if the secret is a name instead of an OCID.
     */
    private final String f_sCompartmentOCID;
    }