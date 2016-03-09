#include <stdio.h>
#include <stdlib.h>

int main()
{
	/* no bug
    int *c	= (int*)malloc(2*sizeof(int));
	c[0] = 0;
	c[1] = 1;
	printf("%d",c[1]);
	free(c);
	*/

	/* right order: def->use->free*/

	/* bug1 no free():  def->use
	int *c	= (int*)malloc(2*sizeof(int));
	c[0] = 0;
	c[1] = 1;
	printf("%d",c[1]);
	*/

	/* bug2 two free(): def->use->free->free
    int *c	= (int*)malloc(2*sizeof(int));
	c[0] = 0;
	c[1] = 1;
	printf("%d",c[1]);
	free(c);
	free(c);
	*/

	/* bug3 two def():  def->def->use->free
	int *c;	
	c= (int*)malloc(2*sizeof(int));
	c= (int*)malloc(2*sizeof(int));
	c[0] = 0;
	c[1] = 1;
	printf("%d",c[1]);
	free(c);
	*/

}
