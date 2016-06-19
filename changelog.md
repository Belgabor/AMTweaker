Changelog
=========

Version 1.?
-----------

* Immersive Engineering heater can now work on the SS2 Fluid Furnace, Large Furnace and Food Smokers, the latter is off by default.


Version 1.0
-----------
* API updates to AMT v2.9m
* Add: Processor tier
* Add: 'soup' command to list soup types for fondue recipes and 'source' command to list fondue sources
* Add: fondue source recipes
* Change: Chocolate Fondue to generalized Fondue recipes, 'choco' command changed to 'fondue'
* Add: EconomicalMilkTea support (diesel generator fuel, orders)
* Add: MCEconomy2 support (Price manipulation, both selling and (in a limited fashion) shop prices)
* Add: Sextiary Sector 2 support (all machines, .agic fuel and sandpit drops)
* Add: Some vanilla support (tool durability, block harvest level/tool)
* Add: Several new commands, including one for Caveworld 2
* Change: AMT is no longer a hard dependency


Version 0.5
-----------
* Update to AMT v2.6c (required)
* Add: Barrel brewing recipes
* Add: recipes command (list all AMT recipes to the log)
* Fix: Some typos


Version 0.4alpha
----------------
* Update to AMT API 2.6 (v2.4c+)
* Add: Chocolate Fondue recipe add/remove
* Add: choco command (lists chocolate fondue recipes)


Version 0.31alpha
-----------------
* Add: Processor recipe removal supports meta/damage wildcard now


Version 0.3alpha
----------------
* Update to AMT API additions in 2.4a
* Add: Some convenience function overloads
* Add: Lots of input sanity checks as script typos can end in unexpected null values passed to functions
* Add: Configurable bamboo basket
* Fix: NPE if evaporator had no item output


Version 0.2alpha
----------------
* Update to AMT API 2.5 (v2.3c+)
* Add: heat and slag command
* Add: slag loot can now be removed
* Fix: commands disappearing after /mt reload
* Fix: Stuck heat source and slag loot additions


Version 0.1alpha
----------------
Early alpha version

* Clay Pan (add/remove recipe, add heat source)
* Cooking Iron Plate (add/remove recipe, add heat source)
* Evaporator (add/remove recipe)
* Food Processor/Jaw Crusher (add/remove recipe)
* Ice Maker (add/remove recipe, register/unregister charge item)
* Slag (add loot)
* Tea Maker (add/remove recipe)
* Command: '/minetweaker amt charge' - List charge items
* Batteries (register/unregister)

