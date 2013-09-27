all:
	make -C servercode compileserver
	make -C tcpcode compiletcp
	make -C clientsrc compileclient
	make -C carcode compilecar
	make -C flightcode compileflight
	make -C hotelcode compilehotel
	make -C customercode compilecustomer

clean:
	make -C servercode clean
	make -C tcpcode clean
	make -C clientsrc clean
	make -C carcode clean
	make -C flightcode clean
	make -C hotelcode clean
	make -C customercode clean
