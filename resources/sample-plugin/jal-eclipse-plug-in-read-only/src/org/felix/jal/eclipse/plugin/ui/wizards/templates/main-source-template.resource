-- ------------------------------------------------------
-- Title: ${title}
--
-- Author: ${author} 
--
-- Adapted-by:
--
-- Compiler: 2.4o
--
-- Description:
-- ${description}
--
-- Sources:
--
-- Notes:
--  - File creation date/time: ${datetime}.
-- ------------------------------------------------------

--;@main
include ${picmicro}                    -- target PICmicro

-- Resonator or crystal connected to pins OSC1 and OSC2.
pragma target clock ${frequency}     -- oscillator frequency

-- configuration memory settings (fuses)
pragma target OSC  HS              -- HS crystal or resonator
pragma target WDT  disabled        -- no watchdog
pragma target LVP  disabled        -- no Low Voltage Programming
pragma target MCLR external        -- reset externally
-- These configuration bit settings are only a selection, sufficient for
-- this program, but other programs may need more or different settings.

enable_digital_io()                -- make all pins digital I/O

-- Specify the pin to which the LED (with serial resistor!) is connected:
alias   led      is pin_A0
pin_A0_direction =  output

forever loop
   led = on
   _usec_delay(250_000)
   led = off
   _usec_delay(250_000)
end loop
