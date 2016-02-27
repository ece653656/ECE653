#include <stdio.h>
#include <stdlib.h>
#include <netware.h>
#include <screen.h>
/*
#include "testutil.h"
*/

/* function to keep the screen open if not launched from bash */
void _NonAppStop( void )
{
  if (getenv("_IN_NETWARE_BASH_") == NULL) {
    uint16_t row, col;

    GetScreenSize(&row, &col);
    gotorowcol(row-1, 0);
    printf("<Press any key to close screen> ");
    getcharacter();
  }
}

/*
static void test_not_impl(CuTest *tc)
{
    CuNotImpl(tc, "Test not implemented on this platform yet");
}
*/

