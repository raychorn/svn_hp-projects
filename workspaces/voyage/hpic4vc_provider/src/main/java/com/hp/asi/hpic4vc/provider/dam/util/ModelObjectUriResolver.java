package com.hp.asi.hpic4vc.provider.dam.util;

import java.net.URI;
import java.net.URISyntaxException;

import com.vmware.vise.data.uri.ResourceTypeResolver;

/**
 * Resolver of URIs referencing ModelObject, i.e. the resource objects of this sample.
 *
 * This resolver is registered in the BaseDamProvider's constructor.
 * DataService will extract type and serverguid of resource objects to appropriately
 * route queries to an associated adapter.
 *
 * A typical URI that can be resolved has the form <scheme>:<type>:<resourceId>,
 * e.g. 'cr:samples:Chassis:server1/ch-2'
 * <ul>
 * <li> scheme - cr is the URI scheme that this resolver supports.
 * <li> type - Qualified type of the object, e.g. samples:Chassis or samples:Rack.
 * <li> resourceId - unique id in the form serverGuid/objectSpecificId, where
 *       - serverGuid is the Guid of the server from which this object originates.
 *       - objectSpecificId is a unique id of the object within the set of objects
 *       of the same type and originating from the same server.
 *
 *  Note: resourceId cannot contain ":" as the last ":" is used as type delimiter!
 * </ul>
 */
public final class ModelObjectUriResolver implements ResourceTypeResolver {

    public static final String SCHEME = "hp";

    /**
     * The delimiter that is used to separate the part of the URI that contains the
     * type of the resource form the actual resource identification part.
     */
    private static final String TYPE_DELIMITER = ":";

    private static final String FRAGMENT_SEPARATOR = "/";

    @Override
    public String getResourceType(URI uri) {
        if (!isValid(uri)) {
            throwExceptionForInvalidURI(uri);
        }
        return parseSchemeSpecificPart(uri, true);
    }

    @Override
    public String getServerGuid(URI uri) {
        String id = getId(uri);
        int fragmentSeparatorIndex = id.indexOf(FRAGMENT_SEPARATOR);
        if (fragmentSeparatorIndex >= 0) {
            return id.substring(0, fragmentSeparatorIndex);
        } else {
            return null;
        }
    }
    
    public String getObjectSpecificId(URI uri) {
        String id = getId(uri);
        int fragmentSeparatorIndex = id.indexOf(FRAGMENT_SEPARATOR);
        if (fragmentSeparatorIndex >= 0) {
            return id.substring(fragmentSeparatorIndex+1, id.length());
        } else {
            return id;
        }
    }
    

    /**
     * Returns the resource id part of the URI.  Typically the id looks like
     * serverGuid + FRAGMENT_SEPARATER + objectSpecificId.
     *
     * If the URI was created using createUri(type, id), then id is returned.
     *
     * @param uri
     *    URI whose id is required.
     *
     * @return
     *    Id part of the URI.
     */
    public String getId(URI uri) {
        if (!isValid(uri)) {
            throwExceptionForInvalidURI(uri);
        }
        return parseSchemeSpecificPart(uri, false);
    }

    /**
     * Unique identifier of the supplied URI.
     *
     * @param uri
     *    URI valid as per this type whose uid is required.
     *
     * @return
     *    Unique identifier for the URI.
     */
    public String getUid(URI uri) {
        if (!isValid(uri)) {
            throwExceptionForInvalidURI(uri);
        }
        return uri.toString();
    }

    /**
     * Creates a new {@link URI} instance using resolver supported scheme.
     *
     * @param type
     *    The type of the resource that the URI identifies.
     * @param id
     *    The resource identification part. May consists of multiple components
     *    allowed by the URI syntax e.g. path, query, fragment.
     * @return
     *    The newly created URI using the resolver scheme.
     */
    public URI createUri(String type, String id) {
        if (type == null || type.length() < 1) {
            throw new IllegalArgumentException("type must be non-null.");
        }
        if (id == null || id.length() < 1) {
            throw new IllegalArgumentException("id must be non-null.");
        }
        URI uri = null;
        try {
            uri = new URI(SCHEME, type + TYPE_DELIMITER + id, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        return uri;
    }

    /**
     * Returns and Id comprising of the serverGuid and objectId such that the object
     * can be uniquely described within the class of objects with the same type across
     * any server.
     *
     * @param serverGuid
     *       Unique identifier for the server that an object being represented originates.
     *
     * @param objectSpecificId
     *       Identifier unique within the instances of subtype of ModelObject.
     *
     * @return
     *       An identifier which combines supplied pieces of information.
     */
    public String createId(String serverGuid, String objectSpecificId) {
        String id = serverGuid + FRAGMENT_SEPARATOR + objectSpecificId;
        return id;
    }

    /**
     * Checks if the provided URI is valid and uses the default scheme.
     */
    public boolean isValid(URI uri) {
        return uri != null && SCHEME.equals(uri.getScheme());
    }

    /**
     * Parses either the type or the resource identification parts from the scheme
     * specific part of the provided URI.
     *
     * @return the type if parseType is true, the resource id if parseType if false.
     */
    private String parseSchemeSpecificPart(URI uri, boolean parseType) {
        assert (uri != null);
        String ssPart = uri.getSchemeSpecificPart();
        // Use lastIndexOf() because types contain ":" for qualifiers.
        int separatorIndex = ssPart.lastIndexOf(TYPE_DELIMITER);
        if (separatorIndex == -1) {
            String message = "URI " + toString(uri) + " does not contain the '"
                    + TYPE_DELIMITER +"' type delimiter.";
            throw new IllegalArgumentException(message);
        }

        String result;
        if (parseType) {
            result = ssPart.substring(0, separatorIndex);
        } else {
            result = ssPart.substring(separatorIndex + 1);
        }

        return result;
    }

    /**
     * Throws an IllegalArgumentException claiming that this URI is invalid for this
     * resolver.
     */
    private void throwExceptionForInvalidURI(URI uri) {
        throw new IllegalArgumentException(
                   "URI " + toString(uri) + " is invalid for this resolver.");
    }

    /**
     * Null safe toString method for URI.
     */
    private String toString(URI uri) {
        if (uri == null) {
            return null;
        }
        return uri.toString();
    }
}
