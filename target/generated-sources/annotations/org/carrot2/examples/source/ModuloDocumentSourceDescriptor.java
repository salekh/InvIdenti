

// APT-generated file.

package org.carrot2.examples.source;

//Imported for JavaDoc references mostly.
import org.carrot2.util.attribute.*;

import java.util.*;
import javax.annotation.*;

/**
 * Metadata and attributes of the {@link org.carrot2.examples.source.ModuloDocumentSource} component. You can use 
 * this descriptor to obtain metadata, such as human readable name and description, about the component 
 * as a whole as well as about its attributes. Using the {@link #attributeBuilder(Map)}
 * you can obtain a builder for type-safe generation of the attribute maps. Please see the
 * <a href="{@docRoot}/overview-summary.html#setting-attributes">main overview</a> for a complete code example. 
 */
@Generated("Generated from org.carrot2.examples.source.ModuloDocumentSource")
public final class ModuloDocumentSourceDescriptor implements IBindableDescriptor
{
    /**
     * The component class for which this descriptor was generated. 
     */
    public final String bindableClassName = "org.carrot2.examples.source.ModuloDocumentSource";

    /**
     * Attribute prefix used by the component.
     */
    public final String prefix = "";

    /**
     * A one sentence summary of the component. It could be presented as a header of the tool
     * tip of the corresponding UI component.
     */
    public final String title = "An example <code>IDocumentSource</code> that accepts a list of <code>Document</code>s and returns a filtered list (<code>org.carrot2.examples.source.ModuloDocumentSource.modulo</code>)";
    
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
        ownAttrs.add(attributes.query);
        ownAttrs.add(attributes.results);
        ownAttrs.add(attributes.modulo);
        ownAttrs.add(attributes.documents);
        ownAttrs.add(attributes.analyzer);

        final Set<AttributeInfo> allAttrs = new HashSet<AttributeInfo>();
        allAttrs.add(org.carrot2.examples.source.ModuloDocumentSourceDescriptor.attributes.query);
        allAttrs.add(org.carrot2.examples.source.ModuloDocumentSourceDescriptor.attributes.results);
        allAttrs.add(org.carrot2.examples.source.ModuloDocumentSourceDescriptor.attributes.modulo);
        allAttrs.add(org.carrot2.examples.source.ModuloDocumentSourceDescriptor.attributes.documents);
        allAttrs.add(org.carrot2.examples.source.ModuloDocumentSourceDescriptor.attributes.analyzer);

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
     * Constants for all attribute keys of the {@link org.carrot2.examples.source.ModuloDocumentSource} component.
     */
    public static class Keys 
    {
        protected Keys() {} 

        /** Attribute key for: {@link org.carrot2.examples.source.ModuloDocumentSource#query}. */
        public static final String QUERY = "query";
        /** Attribute key for: {@link org.carrot2.examples.source.ModuloDocumentSource#results}. */
        public static final String RESULTS = "results";
        /** Attribute key for: {@link org.carrot2.examples.source.ModuloDocumentSource#modulo}. */
        public static final String MODULO = "org.carrot2.examples.source.ModuloDocumentSource.modulo";
        /** Attribute key for: {@link org.carrot2.examples.source.ModuloDocumentSource#documents}. */
        public static final String DOCUMENTS = "documents";
        /** Attribute key for: {@link org.carrot2.examples.source.ModuloDocumentSource#analyzer}. */
        public static final String ANALYZER = "org.carrot2.examples.source.ModuloDocumentSource.analyzer";
    }


    /* Attribute descriptors. */

    /**
     * All attributes of the {@link org.carrot2.examples.source.ModuloDocumentSource} component.
     */
    public static final class Attributes
    {
        private Attributes() { /* No public instances. */ }

        /**
         *          */
        public final AttributeInfo query = 
            new AttributeInfo(
                "query",
                "org.carrot2.examples.source.ModuloDocumentSource",
                "query",
                "The query won't matter to us but we bind it anyway.",
                null,
                "The query won't matter to us but we bind it anyway",
                null,
                null,
                null,
                null
            );

        /**
         *          */
        public final AttributeInfo results = 
            new AttributeInfo(
                "results",
                "org.carrot2.examples.source.ModuloDocumentSource",
                "results",
                "Maximum number of results to return.",
                null,
                "Maximum number of results to return",
                null,
                null,
                null,
                null
            );

        /**
         *          */
        public final AttributeInfo modulo = 
            new AttributeInfo(
                "org.carrot2.examples.source.ModuloDocumentSource.modulo",
                "org.carrot2.examples.source.ModuloDocumentSource",
                "modulo",
                "Modulo to fetch the documents with. This dummy input attribute is just to show how\ncustom input attributes can be implemented.",
                null,
                "Modulo to fetch the documents with",
                "This dummy input attribute is just to show how custom input attributes can be implemented.",
                null,
                null,
                null
            );

        /**
         *          */
        public final AttributeInfo documents = 
            new AttributeInfo(
                "documents",
                "org.carrot2.examples.source.ModuloDocumentSource",
                "documents",
                "Documents accepted and returned by this document source. \nThe documents are returned in an output \nattribute with key equal to {@link Keys#DOCUMENTS},",
                null,
                "Documents accepted and returned by this document source",
                "The documents are returned in an output attribute with key equal to <code>Keys.DOCUMENTS</code>,",
                null,
                null,
                null
            );

        /**
         *          */
        public final AttributeInfo analyzer = 
            new AttributeInfo(
                "org.carrot2.examples.source.ModuloDocumentSource.analyzer",
                "org.carrot2.examples.source.ModuloDocumentSource",
                "analyzer",
                "A non-primitive attribute do demonstrate the need for\n{@link org.carrot2.util.attribute.constraint.ImplementingClasses} constraint. \nIt must be added to specify\nwhich assignable types are allowed as values for the attribute. To allow all\nassignable values, specify empty \n{@link org.carrot2.util.attribute.constraint.ImplementingClasses#classes()} and\n{@link org.carrot2.util.attribute.constraint.ImplementingClasses#strict()} equal to <code>false</code>.",
                null,
                "A non-primitive attribute do demonstrate the need for <code>org.carrot2.util.attribute.constraint.ImplementingClasses</code> constraint",
                "It must be added to specify which assignable types are allowed as values for the attribute. To allow all assignable values, specify empty <code>org.carrot2.util.attribute.constraint.ImplementingClasses.classes()</code> and <code>org.carrot2.util.attribute.constraint.ImplementingClasses.strict()</code> equal to <code>false</code>.",
                null,
                null,
                null
            );


    }

    /**
     * Attribute map builder for the  {@link org.carrot2.examples.source.ModuloDocumentSource} component. You can use this
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
         * The query won't matter to us but we bind it anyway.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#query 
         */
        public AttributeBuilder query(java.lang.String value)
        {
            map.put("query", value);
            return this;
        }
        
        
        
        
        
        /**
         * The query won't matter to us but we bind it anyway.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#query 
         */
        public AttributeBuilder query(IObjectFactory<? extends java.lang.String> value)
        {
            map.put("query", value);
            return this;
        }
        
        
        
        
        
        /**
         * Maximum number of results to return.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#results 
         */
        public AttributeBuilder results(int value)
        {
            map.put("results", value);
            return this;
        }
        
        
        
        
        
        /**
         * Maximum number of results to return.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#results 
         */
        public AttributeBuilder results(IObjectFactory<? extends java.lang.Integer> value)
        {
            map.put("results", value);
            return this;
        }
        
        
        
        
        
        /**
         * Modulo to fetch the documents with. This dummy input attribute is just to show how
custom input attributes can be implemented.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#modulo 
         */
        public AttributeBuilder modulo(int value)
        {
            map.put("org.carrot2.examples.source.ModuloDocumentSource.modulo", value);
            return this;
        }
        
        
        
        
        
        /**
         * Modulo to fetch the documents with. This dummy input attribute is just to show how
custom input attributes can be implemented.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#modulo 
         */
        public AttributeBuilder modulo(IObjectFactory<? extends java.lang.Integer> value)
        {
            map.put("org.carrot2.examples.source.ModuloDocumentSource.modulo", value);
            return this;
        }
        
        
        
        
        
        /**
         * Documents accepted and returned by this document source. 
The documents are returned in an output 
attribute with key equal to {@link Keys#DOCUMENTS},
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#documents 
         */
        public AttributeBuilder documents(java.util.List<org.carrot2.core.Document> value)
        {
            map.put("documents", value);
            return this;
        }
        
        
        
        
        
        /**
         * Documents accepted and returned by this document source. 
The documents are returned in an output 
attribute with key equal to {@link Keys#DOCUMENTS},
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#documents 
         */
        public AttributeBuilder documents(IObjectFactory<? extends java.util.List<org.carrot2.core.Document>> value)
        {
            map.put("documents", value);
            return this;
        }
        
        
        
        /**
         * Documents accepted and returned by this document source. 
The documents are returned in an output 
attribute with key equal to {@link Keys#DOCUMENTS},
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#documents 
         */
        @SuppressWarnings("unchecked")        public java.util.List<org.carrot2.core.Document> documents()
        {
            return (java.util.List<org.carrot2.core.Document>) map.get("documents");
        }
        
        
        
        /**
         * A non-primitive attribute do demonstrate the need for
{@link org.carrot2.util.attribute.constraint.ImplementingClasses} constraint. 
It must be added to specify
which assignable types are allowed as values for the attribute. To allow all
assignable values, specify empty 
{@link org.carrot2.util.attribute.constraint.ImplementingClasses#classes()} and
{@link org.carrot2.util.attribute.constraint.ImplementingClasses#strict()} equal to <code>false</code>.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#analyzer 
         */
        public AttributeBuilder analyzer(org.apache.lucene.analysis.Analyzer value)
        {
            map.put("org.carrot2.examples.source.ModuloDocumentSource.analyzer", value);
            return this;
        }
        
        
        
        /**
         * A non-primitive attribute do demonstrate the need for
{@link org.carrot2.util.attribute.constraint.ImplementingClasses} constraint. 
It must be added to specify
which assignable types are allowed as values for the attribute. To allow all
assignable values, specify empty 
{@link org.carrot2.util.attribute.constraint.ImplementingClasses#classes()} and
{@link org.carrot2.util.attribute.constraint.ImplementingClasses#strict()} equal to <code>false</code>.
         *
         * A class that extends org.apache.lucene.analysis.Analyzer or appropriate IObjectFactory.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#analyzer
         */
        public AttributeBuilder analyzer(Class<?> clazz)
        {
            map.put("org.carrot2.examples.source.ModuloDocumentSource.analyzer", clazz);
            return this;
        }
        
        
        
        /**
         * A non-primitive attribute do demonstrate the need for
{@link org.carrot2.util.attribute.constraint.ImplementingClasses} constraint. 
It must be added to specify
which assignable types are allowed as values for the attribute. To allow all
assignable values, specify empty 
{@link org.carrot2.util.attribute.constraint.ImplementingClasses#classes()} and
{@link org.carrot2.util.attribute.constraint.ImplementingClasses#strict()} equal to <code>false</code>.
         * 
         * @see org.carrot2.examples.source.ModuloDocumentSource#analyzer 
         */
        public AttributeBuilder analyzer(IObjectFactory<? extends org.apache.lucene.analysis.Analyzer> value)
        {
            map.put("org.carrot2.examples.source.ModuloDocumentSource.analyzer", value);
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
