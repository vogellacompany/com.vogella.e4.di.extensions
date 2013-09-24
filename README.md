Idea
====

Inject all registered OSGi services of a given type at the given moment. No service tracking whatsoever.

Client
======

```
public class NewSomePoorClassHandler {
	@Execute
	public void execute(@AllServices java.util.List<SomePoorClass> spc) {
	    // SomePoorClass happens to be an OSGi service
	}
}
```

Shortcoming
===========

-   List\<OfAnyType\> registered in IEclipseContext takes precedence. This is due to the fact that an ExtendedObjectSupplier is per design called \_after\_ the PrimaryObjectSupplier.

