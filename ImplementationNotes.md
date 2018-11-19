### Resources

* [ResourceBundle.getBundle - Java 9](https://docs.oracle.com/javase/9/docs/api/java/util/ResourceBundle.html#getBundle-java.lang.String-java.util.Locale-java.lang.ClassLoader-java.util.ResourceBundle.Control-)
* [ResourceBundle.getBundle (variant) - Java 9](https://docs.oracle.com/javase/9/docs/api/java/util/ResourceBundle.html#getBundle-java.lang.String-java.util.Locale-java.lang.ClassLoader-)
* [Caching TTL](https://docs.oracle.com/javase/9/docs/api/java/util/ResourceBundle.Control.html#getTimeToLive-java.lang.String-java.util.Locale-)
* [About the ResourceBundle Class (Oracle Docs)](https://docs.oracle.com/javase/tutorial/i18n/resbundle/concept.html)


## For spring support 

* create an impl of org.springframework.context.support.AbstractResourceBasedMessageSource
* Inspiration from org.springframework.context.support.ResourceBundleMessageSource

## TODO

org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator.interpolateMessage 267 may fail


## TODO Caching

* first cache is in here org.hibernate.validator.resourceloading.CachingResourceBundleLocator.getResourceBundle
* then here: java.util.ResourceBundle.getBundleImpl(String baseName, Locale locale,  ClassLoader loader, Control control) 1314

# Other implementations

* [Uses his own impl](https://github.com/jknecht/database-resource-bundle/blob/master/src/main/java/com/jeffknecht/rbtest/common/DatabaseResourceBundleControl.java)
* [Uses ListResourceProperty](https://github.com/myfear/Bundle-Provider-Tricks/blob/master/src/main/java/net/eisele/example/resourcebundletricks/bundles/DatabaseResourceBundle.java)
