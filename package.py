#!/usr/bin/python

import json
import argparse
from subprocess import Popen, STDOUT, PIPE

script = '''
create_project -force "{0}" /tmp
ipx::infer_core -name "{0}" -vendor "{1}" -library "{2}" -version "{3}" "{4}"
set_property supported_families {{virtex7 Production qvirtex7 Production kintex7 Production kintex7l Production qkintex7 Production qkintex7l Production artix7 Production artix7l Production aartix7 Production qartix7 Production zynq Production qzynq Production azynq Production}} [ipx::current_core]
set_property core_revision 1 [ipx::current_core]
set_property display_name "{0}" [ipx::current_core]
set_property description "{0}" [ipx::current_core]
set_property taxonomy "{2}" [ipx::current_core]
ipx::add_bus_parameter POLARITY [ipx::get_bus_interfaces reset -of_objects [ipx::current_core]]
set_property value ACTIVE_HIGH [ipx::get_bus_parameters POLARITY -of_objects [ipx::get_bus_interfaces reset -of_objects [ipx::current_core]]]
ipx::update_checksums [ipx::current_core]
ipx::save_core [ipx::current_core]
close_project -delete
'''

def read_json(jsonfile):
	with open(jsonfile, 'r') as jf:
		contents = jf.read()
		return json.loads(contents)

def make_vivado_script(jsonfile):
	cd = read_json(jsonfile)
	return script.format(cd['name'], cd['vendor'], cd['library'], cd['version'], cd['root'])

def run_vivado(script):
	p = Popen(['vivado', '-mode', 'tcl', '-nolog', '-nojournal'], stdin=PIPE, stdout=PIPE, stderr=STDOUT)
	output = p.communicate(input = script)[0]
	print output.decode() 

def parse_args:
	parser = argparse.ArgumentParser(description = 'Package a hardware module specified by JSON as IP-XACT.')
	parser.add_argument('json', help = 'path to JSON file')
	return parser.parse_args()

args = parse_args
run_vivado(make_vivado_script(args.json))
