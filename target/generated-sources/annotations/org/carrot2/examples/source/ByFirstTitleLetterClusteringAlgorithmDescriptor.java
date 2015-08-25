

// APT-generated file.

package org.carrot2.examples.source;

//Imported for JavaDoc references mostly.
import org.carrot2.util.attribute.*;

import java.util.*;
import javax.annotation.*;

/**
 * Metadata and attributes of the {@link org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm} component. You can use 
 * this descriptor to obtain metadata, such as human readable name and description, about the component 
 * as a whole as well as about its attributes. Using the {@link #attributeBuilder(Map)}
 * you can obtain a builder for type-safe generation of the attribute maps. Please see the
 * <a href="{@docRoot}/overview-summary.html#setting-attributes">main overview</a> for a complete code example. 
 */
@Generated("Generated from org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm")
public final class ByFirstTitleLetterClusteringAlgorithmDescriptor implements IBindableDescriptor
{
    /**
     * The component class for which this descriptor was generated. 
     */
    public final String bindableClassName = "org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm";

    /**
     * Attribute prefix used by the component.
     */
    public final String prefix = "ByFirstLetter";

    /**
     * A one sentence summary of the component. It could be presented as a header of the tool
     * tip of the corresponding UI component.
     */
    public final String title = "An example clustering algorithm component that groups documents by the first letter of their title";
    
    /**
     * A short label for the component. It can be presented as a label of the
     * corresponding UI component.
     */
    public final String label = "";

    /**
     * A longer, possibly multi sentence, description of the component. It could be presented
     * as a body of the tool tip of the corresponding UI component.
     */
    public final String description = "";

    /**
     * Attributes of the component. Note that only statically reachable fields are included.
     * Additional attributes may be available at run time. 
     */
    public final static Attributes attributes; 

    /**
     * Attributes declared directly by the component.
     */
    private final static Set<AttributeInfo> ownAttributes;

    /**
     * Attributes declared by the component or its superclasses.
     */
    private final static Set<AttributeInfo> allAttributes;

    /**
     * Attributes declared by the component or its superclasses, lookup dictionary 
     * by attribute key.
     */
    private final static Map<String, AttributeInfo> allAttributesByKey;

    /**
     * Attributes declared by the component or its superclasses, lookup dictionary by 
     * attribute's field name.
     */
    private final static Map<String, AttributeInfo> allAttributesByFieldName;

    /**
     * Static initializer for internal collections.
     */
    static
    {
        attributes = new Attributes();

        final Set<AttributeInfo> ownAttrs = new HashSet<AttributeInfo>();
        ownAttrs.add(attributes.documents);
        ownAttrs.add(attributes.clusters);
        ownAttrs.add(attributes.caseSensitive);

        final Set<AttributeInfo> allAttrs = new HashSet<AttributeInfo>();
        allAttrs.add(org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithmDescriptor.attributes.documents);
        allAttrs.add(org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithmDescriptor.attributes.clusters);
        allAttrs.add(org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithmDescriptor.attributes.caseSensitive);

        allAttributes = Collections.unmodifiableSet(allAttrs);
        ownAttributes = Collections.unmodifiableSet(ownAttrs);
        
        final Map<String, AttributeInfo> allAttrsByKey = new HashMap<String, AttributeInfo>();
        final Map<String, AttributeInfo> allAttrsByFieldName = new HashMap<String, AttributeInfo>();
        for (AttributeInfo ai : allAttrs)
        {
            allAttrsByKey.put(ai.key, ai);
            allAttrsByFieldName.put(ai.fieldName, ai);
        }

        allAttributesByKey = Collections.unmodifiableMap(allAttrsByKey);
        allAttributesByFieldName = Collections.unmodifiableMap(allAttrsByFieldName);
    }

    
    /* Attribute keys. */

    /**
     * Constants for all attribute keys of the {@link org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm} component.
     */
    public static class Keys 
    {
        protected Keys() {} 

        /** Attribute key for: {@link org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#documents}. */
        public static final String DOCUMENTS = "documents";
        /** Attribute key for: {@link org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#clusters}. */
        public static final String CLUSTERS = "clusters";
        /** Attribute key for: {@link org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#caseSensitive}. */
        public static final String CASE_SENSITIVE = "ByFirstLetter.caseSensitive";
    }


    /* Attribute descriptors. */

    /**
     * All attributes of the {@link org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm} component.
     */
    public static final class Attributes
    {
        private Attributes() { /* No public instances. */ }

        /**
         * 
         * 
         * @see org.carrot2.core.attribute.CommonAttributes#documents
         */
        public final AttributeInfo documents = 
            new AttributeInfo(
                "documents",
                "org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm",
                "documents",
                "Documents to cluster.",
                null,
                "Documents to cluster",
                null,
                null,
                null,
                org.carrot2.core.attribute.CommonAttributesDescriptor.attributes.documents
            );

        /**
         * 
         * 
         * @see org.carrot2.core.attribute.CommonAttributes#clusters
         */
        public final AttributeInfo clusters = 
            new AttributeInfo(
                "clusters",
                "org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm",
                "clusters",
                "Clusters created by the algorithm.",
                null,
                "Clusters created by the algorithm",
                null,
                null,
                null,
                org.carrot2.core.attribute.CommonAttributesDescriptor.attributes.clusters
            );

        /**
         *          */
        public final AttributeInfo caseSensitive = 
            new AttributeInfo(
                "ByFirstLetter.caseSensitive",
                "org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm",
                "caseSensitive",
                "Whether to group case-insensitive codepoints together.",
                null,
                "Whether to group case-insensitive codepoints together",
                null,
                null,
                null,
                null
            );


    }

    /**
     * Attribute map builder for the  {@link org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm} component. You can use this
     * builder as a type-safe alternative to populating the attribute map using attribute keys.
     */
    public static class AttributeBuilder 
    {
        /** The attribute map populated by this builder. */
        public final Map<String, Object> map;

        /**
         * Creates a builder backed by the provided map.
         */
        protected AttributeBuilder(Map<String, Object> map)
        {
            
            this.map = map;
        }


        
        
        /**
         * Documents to cluster.
         * 
         * @see org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#documents 
         */
        public AttributeBuilder documents(java.util.List<org.carrot2.core.Document> value)
        {
            map.put("documents", value);
            return this;
        }
        
        
        
        
        
        /**
         * Documents to cluster.
         * 
         * @see org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#documents 
         */
        public AttributeBuilder documents(IObjectFactory<? extends java.util.List<org.carrot2.core.Document>> value)
        {
            map.put("documents", value);
            return this;
        }
        
        
        
        
        
        
        
        
        
        
        
        /**
         * Clusters created by the algorithm.
         * 
         * @see org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#clusters 
         */
        @SuppressWarnings("unchecked")        public java.util.List<org.carrot2.core.Cluster> clusters()
        {
            return (java.util.List<org.carrot2.core.Cluster>) map.get("clusters");
        }
        
        
        
        /**
         * Whether to group case-insensitive codepoints together.
         * 
         * @see org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#caseSensitive 
         */
        public AttributeBuilder caseSensitive(boolean value)
        {
            map.put("ByFirstLetter.caseSensitive", value);
            return this;
        }
        
        
        
        
        
        /**
         * Whether to group case-insensitive codepoints together.
         * 
         * @see org.carrot2.examples.source.ByFirstTitleLetterClusteringAlgorithm#caseSensitive 
         */
        public AttributeBuilder caseSensitive(IObjectFactory<? extends java.lang.Boolean> value)
        {
            map.put("ByFirstLetter.caseSensitive", value);
            return this;
        }
        
        
        
    }

    /**
     * Creates an attribute map builder for the component. You can use this
     * builder as a type-safe alternative to populating the attribute map using attribute keys.
     * 
     * @param attributeValues An existing map which should be used to collect attribute values. 
     *        Attribute values set by this builder will be added to the provided map, overwriting
     *        previously defined mappings, if any.
     */
    public static AttributeBuilder attributeBuilder(Map<String, Object> attributeValues)
    {
        return new AttributeBuilder(attributeValues);
    }
    
    /* IBindableDescriptor */

    @Override 
    public String getPrefix()
    {
        return prefix;
    }

    @Override 
    public String getTitle()
    {
        return title;
    }
    
    @Override 
    public String getLabel()      
    { 
        return label;
    }
    
    @Override 
    public String getDescription() 
    { 
        return description; 
    }

    @Override 
    public Set<AttributeInfo> getOwnAttributes()
    { 
        return ownAttributes;
    }

    @Override 
    public Set<AttributeInfo> getAttributes()
    {
        return allAttributes;
    }

    @Override 
    public Map<String, AttributeInfo> getAttributesByKey()
    {
        return allAttributesByKey;
    }

    @Override 
    public Map<String, AttributeInfo> getAttributesByFieldName()
    {
        return allAttributesByFieldName;
    }
}
