# Database Resource Bundle

Database backed implementation of `java.util.ResourceBundle`. With this implementation resource bundle contents are loaded from a database table instead of `.properties` files.

# Usage

First you need to have a database table which can be created by `/resources/create.sql`. In this case you can use `DefaultBundleContentLoaderStrategy` without any customization.

You can normally use default implementation of `java.util.ResourceBundle` which uses `.properties` files by default as following static methods of `ResourceBundle` class:

```
// Static methods of `java.util.ResourceBundle`
static ResourceBundle getBundle​(String baseName)
static ResourceBundle getBundle(String baseName, Locale locale)
static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader)
static ResourceBundle getBundle(String baseName, Locale targetLocale, ClassLoader loader, ResourceBundle.Control control)
static ResourceBundle getBundle(String baseName, Locale targetLocale, ResourceBundle.Control control)
static ResourceBundle getBundle(String baseName, ResourceBundle.Control control)
```

To get an instance of `DatabaseResourceBundle` you can use static methods above which has `ResourceBundle.Control` as an argument because `ResourceBundle.Control` class is responsible of content loading.

In other words you can create `DatabaseResourceBundle` instances via following static methods if you supply a `DatabaseResourceBundleControl` instance as `ResourceBundle.Control` parameter.

```
// Static methods of `java.util.ResourceBundle`
static ResourceBundle getBundle(String baseName, Locale targetLocale, ClassLoader loader, ResourceBundle.Control control)
static ResourceBundle getBundle(String baseName, Locale targetLocale, ResourceBundle.Control control)
static ResourceBundle getBundle(String baseName, ResourceBundle.Control control)
```

There are also two static methods to load `DatabaseResourceBundle` instances in `DatabaseResourceBundle` class:

```
// Static methods of `DatabaseResourceBundle`
public static ResourceBundle getBundle(String baseName, DataSource dataSource)
public static ResourceBundle getBundle(String baseName, Locale locale, DataSource dataSource)
```

Above two methods creates `DatabaseResourceBundle` without `DatabaseResourceBundleControl` parameters. Those methods create `DatabaseResourceBundleControl` implicitly and eventually return `DatabaseResourceBundle` instances. 

## Code Example

```java
ResourceBundle.Control control = new DatabaseResourceBundleControl(new DefaultBundleContentLoaderStrategy(dataSource));
ResourceBundle rb = ResourceBundle.getBundle("messages", locale, control);
rb.getString("demo.username")
```

## Usage in Spring Boot

There is a Spring Boot starter which configures a `message source` backed by `DatabaseResourceBundle`.
You can [find it here](https://github.com/kodgemisi/database-resource-bundle-message-source-starter). 

# LICENSE

© Copyright 2018 Kod Gemisi Ltd.

Mozilla Public License 2.0 (MPL-2.0)

https://tldrlegal.com/license/mozilla-public-license-2.0-(mpl-2)

MPL is a copyleft license that is easy to comply with. You must make the source code for any of your changes available under MPL, 
but you can combine the MPL software with proprietary code, as long as you keep the MPL code in separate files. 
Version 2.0 is, by default, compatible with LGPL and GPL version 2 or greater. You can distribute binaries under a proprietary license, 
as long as you make the source available under MPL.

[See Full License Here](https://www.mozilla.org/en-US/MPL/2.0/)