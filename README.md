CompassLibrary
==============

An Android library containing a "Compass Activity" taken from [c:geo](https://github.com/cgeo/cgeo) project.

### Usage

After you imported the library, you can start the compass Activity like that:
```
CompassActivity.startActivity(this, new Geopoint(currentPoint.getLocation()), currentPoint.getName());

```

If you want to use CompassActivity with ActionBarSherlock or something like that you can copy CompassActivity on your project and extend the right class.

### Thanks

Thank you to the c:geo team.

### License

Apache 2 see LICENSE
